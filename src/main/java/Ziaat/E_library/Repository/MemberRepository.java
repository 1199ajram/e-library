package Ziaat.E_library.Repository;

import Ziaat.E_library.Model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByMembershipNumber(String membershipNumber);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUserId(Long userId);

    @Query("SELECT m FROM Member m WHERE " +
            "LOWER(m.firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(m.lastname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(m.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(m.membershipNumber) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Member> searchMembers(String search, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE LOWER(m.firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR m.lastname LIKE CONCAT('%', :search, '%')")
    List<Member> searchMembers(@Param("search") String search);

    Long countByStatus(Member.MemberStatus status);
}
