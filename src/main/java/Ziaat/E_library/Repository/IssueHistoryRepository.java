package Ziaat.E_library.Repository;

import Ziaat.E_library.Model.IssueHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface IssueHistoryRepository extends JpaRepository<IssueHistory, UUID> {

    @Query("SELECT ih FROM IssueHistory ih WHERE ih.book.bookId = :bookId ORDER BY ih.issueDate DESC")
    List<IssueHistory> findByBookIdOrderByIssueDateDesc(UUID bookId);

    List<IssueHistory> findByBookCopy_CopyIdAndReturnedFalse(UUID copyId);

    // Add to IssueHistoryRepository
    @Query("SELECT ih FROM IssueHistory ih WHERE ih.returned = false AND ih.dueDate < CURRENT_TIMESTAMP")
    List<IssueHistory> findOverdueIssues();

    List<IssueHistory> findByMember_MemberIdAndReturnedFalse(UUID memberId);

    // Find current issues (not returned) for a member
    List<IssueHistory> findByMemberMemberIdAndReturnedFalse(UUID memberId);

    // Find all issues for a member with pagination
    Page<IssueHistory> findByMemberMemberId(UUID memberId, Pageable pageable);

    // Find returned books
    List<IssueHistory> findByReturnedTrue();

    // Find overdue issues
    @Query("SELECT i FROM IssueHistory i WHERE i.returned = false AND i.dueDate < :currentDate")
    List<IssueHistory> findOverdueIssues(@Param("currentDate") LocalDateTime currentDate);

    // Count current issues by member
    @Query("SELECT COUNT(i) FROM IssueHistory i WHERE i.member.memberId = :memberId AND i.returned = false")
    long countCurrentIssuesByMember(@Param("memberId") UUID memberId);

    // Check if book copy is currently issued
    boolean existsByBookCopyCopyIdAndReturnedFalse(UUID copyId);

    // Find by member and returned status
    List<IssueHistory> findByMemberMemberIdAndReturned(UUID memberId, Boolean returned);

}