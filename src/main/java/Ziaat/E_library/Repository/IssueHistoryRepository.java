package Ziaat.E_library.Repository;

import Ziaat.E_library.Model.IssueHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}