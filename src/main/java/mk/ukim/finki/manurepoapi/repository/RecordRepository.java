package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.Record;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {

    Boolean existsByIdAndApprovedTrueAndPrivateRecordFalse(Long recordId);

    @EntityGraph(value = "Record.authorAccounts")
    Optional<Record> findByIdAndApprovedTrueAndPrivateRecordFalse(Long recordId);

    boolean existsByIdAndAuthorAccountsContaining(Long recordId, Account account);

}
