package Ziaat.E_library.Repository;

import Ziaat.E_library.Model.Author;
import Ziaat.E_library.Model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByMember_MemberIdAndStatus(UUID memberId, Reservation.ReservationStatus status);
    List<Reservation> findByBook_BookIdAndStatus(UUID bookId, Reservation.ReservationStatus status);
    Long countByStatusAndBook_BookId(Reservation.ReservationStatus status, UUID bookId);

    // Custom query for more complex search
    @Query("""
    SELECT a FROM Reservation a
    WHERE a.status = :status AND (
        LOWER(a.book.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(CONCAT(a.book.publisher.firstname, ' ', a.book.publisher.lastname)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(CONCAT(a.member.firstname, ' ', a.member.lastname)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR CAST(a.reservationDate AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR CAST(a.expiryDate AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    )
""")
    Page<Reservation> searchReservation(
            @Param("searchTerm") String searchTerm,
            @Param("status") Reservation.ReservationStatus status,
            Pageable pageable
    );

    @Query("""
    SELECT a FROM Reservation a
    WHERE 
        LOWER(a.book.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(CONCAT(a.book.publisher.firstname, ' ', a.book.publisher.lastname)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(CONCAT(a.member.firstname, ' ', a.member.lastname)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR CAST(a.reservationDate AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR CAST(a.expiryDate AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
""")
    Page<Reservation> searchReservationWithoutStatus(@Param("searchTerm") String searchTerm, Pageable pageable);


    // Get all with pagination (already available from JpaRepository)
    Page<Reservation> findAllByStatus(Reservation.ReservationStatus status, Pageable pageable);

}