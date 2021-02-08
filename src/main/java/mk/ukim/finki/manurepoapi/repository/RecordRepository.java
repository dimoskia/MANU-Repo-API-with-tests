package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.Record;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {

    Boolean existsByIdAndApprovedTrueAndPrivateRecordFalse(Long recordId);

    @EntityGraph(value = "Record.authorAccounts")
    Optional<Record> findByIdAndApprovedTrueAndPrivateRecordFalse(Long recordId);

    @EntityGraph(value = "Record.authorAccounts")
    @Query("SELECT r from Record r WHERE r.id = :recordId")
    Optional<Record> fetchRecordWithAuthors(@Param("recordId") Long recordId);

    boolean existsByIdAndAuthorAccountsContaining(Long recordId, Account account);

    Optional<Record> findByIdAndAuthorAccountsContaining(Long recordId, Account account);

    @Modifying
    @Query("UPDATE Record r SET r.downloadsCount = r.downloadsCount + 1 WHERE r.id = :recordId")
    void incrementDownloads(@Param(value = "recordId") Long recordId);

}
