from locust import HttpUser, task, between
import random
import time


class TicketBot(HttpUser):

    wait_time = between(0.1, 0.5)

    def on_start(self):
        """
        각 가상 유저에게 고유 userSq 부여
        """
        self.user_sq = random.randint(1000, 999999)
        self.round_sq = 6

    @task
    def ticketing(self):

        headers = {
            "Content-Type": "application/json",
            "X-USER-SQ": str(self.user_sq)
        }

        # 예매 진입 시도
        with self.client.post(
            "/bookings/enter",
            json={"roundSq": self.round_sq},
            headers=headers,
            catch_response=True
        ) as response:

            if response.status_code != 200:
                response.failure(f"Enter API HTTP {response.status_code}")
                return

            data = response.json()
            status = data.get("status")

            # ENTER 즉시 입장
            if status == "ENTER":
                response.success()
                print(f"{self.user_sq} ENTER immediately")
                return

            # WAITING -> 정상 동작
            elif status == "WAITING":
                response.success()
                print(f"{self.user_sq} waiting...")

                # 대기열 폴링
                for _ in range(60):  # 최대 30초 polling

                    with self.client.get(
                        f"/queue/status?roundSq={self.round_sq}",
                        headers={"X-USER-SQ": str(self.user_sq)},
                        catch_response=True
                    ) as status_res:

                        if status_res.status_code != 200:
                            status_res.failure(
                                f"Status HTTP {status_res.status_code}"
                            )
                            return

                        status_data = status_res.json()

                        if status_data.get("status") == "ENTER":
                            status_res.success()
                            print(f"{self.user_sq} promoted")
                            return
                        else:
                            status_res.success()

                    time.sleep(0.5)

                return

            # CLOSED
            elif status == "CLOSED":
                response.success()
                print(f"{self.user_sq} closed")
                return

            else:
                response.failure(f"Unknown status: {status}")
