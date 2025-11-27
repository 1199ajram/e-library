package Ziaat.E_library.Services;

import Ziaat.E_library.Dto.MemberResponse;
import Ziaat.E_library.Dto.ReservationRequest;
import Ziaat.E_library.Dto.BookDto;
import Ziaat.E_library.Dto.ReservationResponseDto;
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

    // ---------------------------------------------------------
    // CREATE RESERVATION
    // ---------------------------------------------------------
    public ReservationResponseDto createReservation(ReservationRequest request) {

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

        reservation = reservationRepository.save(reservation);

        return mapToDto(reservation);
    }

    // ---------------------------------------------------------
    // GET ALL BY STATUS + SEARCH + PAGINATION
    // ---------------------------------------------------------
    public Page<ReservationResponseDto> getAllReservationByStatus(
            int page, int size, String sortBy, String sortDir,
            String search, Reservation.ReservationStatus status) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Reservation> reservationPage;

        if (search != null && !search.trim().isEmpty()) {
            if (status != null) {
                reservationPage = reservationRepository.searchReservation(search, status, pageable);
            } else {
                reservationPage = reservationRepository.searchReservationWithoutStatus(search, pageable);
            }
        } else {
            if (status != null) {
                reservationPage = reservationRepository.findAllByStatus(status, pageable);
            } else {
                reservationPage = reservationRepository.findAll(pageable);
            }
        }

        return reservationPage.map(this::mapToDto);
    }

    // ---------------------------------------------------------
    // MAP ENTITY TO DTO
    // ---------------------------------------------------------
    private ReservationResponseDto mapToDto(Reservation reservation) {
        ReservationResponseDto dto = new ReservationResponseDto();

        // Reservation basic fields
        dto.setReservationId(reservation.getReservationId());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setExpiryDate(reservation.getExpiryDate());
        dto.setStatus(reservation.getStatus().name());
        dto.setNotes(reservation.getNotes());

        // ---------------- BOOK DETAILS ----------------
        if (reservation.getBook() != null) {
            Books book = reservation.getBook();

            BookDto bookDto = new BookDto();
            bookDto.setBookId(book.getBookId());
            bookDto.setTitle(book.getTitle());
            bookDto.setIsbn(book.getIsbn());
            bookDto.setPublishedDate(book.getPublishedDate());
            bookDto.setDescription(book.getDescription());
            bookDto.setLanguage(book.getLanguage());
            bookDto.setPageCount(book.getPageCount());
            bookDto.setCoverImageUrl(book.getCoverImageUrl());
            bookDto.setAttachmentUrl(book.getAttachmentUrl());

            dto.setBook(bookDto);

            dto.setBookId(book.getBookId());
            dto.setBookTitle(book.getTitle());
            dto.setBookCoverUrl(book.getCoverImageUrl());
            dto.setCoverImageUrl(book.getCoverImageUrl());
        }

        // ---------------- MEMBER DETAILS ----------------
        if (reservation.getMember() != null) {
            Member member = reservation.getMember();

            MemberResponse memberDto = new MemberResponse();
            memberDto.setMemberId(member.getMemberId().toString());
            memberDto.setMembershipNumber(member.getMembershipNumber());
            memberDto.setFirstname(member.getFirstname());
            memberDto.setLastname(member.getLastname());
            memberDto.setEmail(member.getEmail());
            memberDto.setPhone(member.getPhone());
            memberDto.setAddress(member.getAddress());
            memberDto.setDateOfBirth(member.getDateOfBirth());
            memberDto.setMembershipStartDate(member.getMembershipStartDate());
            memberDto.setMembershipEndDate(member.getMembershipEndDate());
            memberDto.setMembershipType(member.getMembershipType().name());
            memberDto.setStatus(member.getStatus().name());
            memberDto.setMaxBooksAllowed(member.getMaxBooksAllowed());
            memberDto.setFineBalance(member.getFineBalance());

            dto.setMember(memberDto);

            dto.setMemberId(member.getMemberId());
            dto.setMemberName(member.getFirstname() + " " + member.getLastname());
            dto.setMemberEmail(member.getEmail());
        }

        return dto;
    }

    // ---------------------------------------------------------
    // MEMBER RESERVATIONS
    // ---------------------------------------------------------
    public List<ReservationResponseDto> getReservationsByMember(UUID memberId) {
        return reservationRepository
                .findByMember_MemberIdAndStatus(memberId, Reservation.ReservationStatus.PENDING)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    // ---------------------------------------------------------
    // BOOK RESERVATIONS
    // ---------------------------------------------------------
    public List<ReservationResponseDto> getReservationsByBook(UUID bookId) {
        return reservationRepository
                .findByBook_BookIdAndStatus(bookId, Reservation.ReservationStatus.PENDING)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    // ---------------------------------------------------------
    // CANCEL RESERVATION
    // ---------------------------------------------------------
    public ReservationResponseDto cancelReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservation = reservationRepository.save(reservation);

        return mapToDto(reservation);
    }

    // ---------------------------------------------------------
    // FULFILL RESERVATION
    // ---------------------------------------------------------
    public ReservationResponseDto fulfillReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(Reservation.ReservationStatus.FULFILLED);
        reservation = reservationRepository.save(reservation);

        return mapToDto(reservation);
    }
}
