package Ziaat.E_library.Repository;

import Ziaat.E_library.Model.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FineRepository extends JpaRepository<Fine, UUID> {
    List<Fine> findByMember_MemberIdOrderByCreatedAtDesc(UUID memberId);
    List<Fine> findByStatus(Fine.FineStatus status);

    @Query("SELECT SUM(f.amount) FROM Fine f WHERE f.member.memberId = :memberId AND f.status = 'UNPAID'")
    Double getTotalUnpaidFinesByMember(UUID memberId);

    List<Fine> findByMember_MemberId(UUID memberId);

    List<Fine> findByMember_MemberIdAndStatus(UUID memberId, Fine.FineStatus status);

    @Query("SELECT f FROM Fine f LEFT JOIN FETCH f.issue i LEFT JOIN FETCH i.book WHERE f.member.memberId = :memberId")
    List<Fine> findByMemberIdWithBookDetails(@Param("memberId") UUID memberId);

    @Query("SELECT SUM(f.amount) FROM Fine f WHERE f.member.memberId = :memberId AND f.status = :status")
    Double sumAmountByMemberIdAndStatus(@Param("memberId") UUID memberId, @Param("status") Fine.FineStatus status);

}