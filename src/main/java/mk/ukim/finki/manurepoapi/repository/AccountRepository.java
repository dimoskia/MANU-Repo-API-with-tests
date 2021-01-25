package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.repository.projection.MemberProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Optional<Account> findByIdAndEnabledTrue(Long accountId);

    @Query("SELECT a.id as id, concat(a.firstName, ' ', a.lastName) as fullName " +
            "FROM Account a " +
            "WHERE (lower(a.firstName) like lower(concat(:term, '%')) or " +
            "lower(a.lastName) like lower(concat(:term, '%'))) and a.enabled = true " +
            "ORDER BY a.firstName, a.lastName")
    List<MemberProjection> searchByName(@Param("term") String searchTerm, Pageable pageable);

    Boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE Account a set a.enabled = true where a.id = :accountId")
    void enableAccount(@Param("accountId") Long accountId);

    @Modifying
    @Query(value = "delete from account a where a.enabled = false " +
            "and a.id not in (select v.account_id from verification_token v)", nativeQuery = true)
    void deleteExpiredAccounts();

}
