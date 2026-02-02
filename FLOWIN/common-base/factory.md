다음 두 클래스를 추가하고 사용
@Component
public class {팩토리 이름} { //이건 앞으로 예외를 만들어줄 팩토리

    private final PortConfig portConfig;

    public {팩토리 이름}(PortConfig portConfig) {
        this.portConfig = portConfig;
    }

    
    public {예외이름} {함수이름}(String message, HttpStatus status) {
        return new AccountServerException(
            message,
            status <- 늘 쓰는 그거(HttpStatus)
            portConfig.getPort()
        );
    }
}

이거는 실제 포트를 뽑아주는 로직
@Component
public class PortConfig implements ApplicationListener<WebServerInitializedEvent> {

    private int port;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.port = event.getWebServer().getPort();
    }

    public int getPort() {
        return port;
    }
}

이 후 코드내에 custombase를 상속받아서 자신의 도메인의 독자적 예외를 작성, 그를 핸들러로 받아서 사용