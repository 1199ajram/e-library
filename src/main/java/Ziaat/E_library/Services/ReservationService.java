package Ziaat.E_library.Services;

import Ziaat.E_library.Dto.ReservationRequest;
import Ziaat.E_library.Model.Author;
import Ziaat.E_library.Model.Books;
import Ziaat.E_library.Model.Member;
import Ziaat.E_library.Model.Reservation;
import Ziaat.E_library.Repository.BookRepository;
import Ziaat.E_library.Repository.MemberRepository;
import Ziaat.E_library.Repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public Reservation createReservation(ReservationRequest request) {
        Books book = bookRepository.findById(UUID.fromString(request.getBookId()))
                .orElseThrow(() -> new RuntimeException("Book not found"));
        Member member = memberRepository.findById(UUID.fromString(request.getMemberId()))
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setMember(member);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setExpiryDate(LocalDateTime.now().plusDays(7));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setNotes(request.getNotes());

        return reservationRepository.save(reservation);
    }

    public Page<Reservation> getAllReservationByStatus(
            int page, int size, String sortBy, String sortDir,
            String search, Reservation.ReservationStatus status) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // ✅ Case 1: If a search term is provided
        if (search != null && !search.trim().isEmpty()) {
            if (status != null) {
                return reservationRepository.searchReservation(search, status, pageable);
            } else {
                return reservationRepository.searchReservationWithoutStatus(search, pageable);
            }
        }

        // ✅ Case 2: No search term
        if (status != null) {
            return reservationRepository.findAllByStatus(status, pageable);
        } else {
            return reservationRepository.findAll(pageable);
        }
    }



    public List<Reservation> getReservationsByMember(UUID memberId) {
        return reservationRepository.findByMember_MemberIdAndStatus(
                memberId, Reservation.ReservationStatus.PENDING);
    }

    public List<Reservation> getReservationsByBook(UUID bookId) {
        return reservationRepository.findByBook_BookIdAndStatus(
                bookId, Reservation.ReservationStatus.PENDING);
    }

    public void cancelReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    public void fulfillReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus(Reservation.ReservationStatus.FULFILLED);
        reservationRepository.save(reservation);
    }
}
