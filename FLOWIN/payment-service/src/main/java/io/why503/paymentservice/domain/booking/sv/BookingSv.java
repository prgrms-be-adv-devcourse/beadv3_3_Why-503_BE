package io.why503.paymentservice.domain.booking.sv;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
import io.why503.paymentservice.domain.booking.model.dto.BookingReqDto;
import io.why503.paymentservice.domain.booking.model.dto.BookingResDto;
import io.why503.paymentservice.domain.booking.model.ett.Booking;
import io.why503.paymentservice.domain.booking.repo.BookingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 성능 최적화
public class BookingSv {

    private final BookingRepo bookingRepo;
    private final BookingMapper bookingMapper;

    // 1. 예매 생성
    // - Mapper를 통해 Entity 변환 후 저장 (Cascade로 Ticket도 자동 저장)
    @Transactional // 쓰기 허용
    public BookingResDto createBooking(BookingReqDto req) {
        Booking booking = bookingMapper.toEntity(req);
        Booking savedBooking = bookingRepo.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    // 2. 예매 상세 조회
    public BookingResDto getBooking(Long bookingSq) {
        Booking booking = bookingRepo.findById(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 번호입니다: " + bookingSq));
        return bookingMapper.toDto(booking);
    }

    // 3. 예매 취소
    // - 상태값 변경 (Dirty Checking으로 자동 Update)
    @Transactional // 쓰기 허용
    public void cancelBooking(Long bookingSq) {
        Booking booking = bookingRepo.findById(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 번호입니다: " + bookingSq));
        booking.cancel(); // status: 0 -> 2
    }
}
