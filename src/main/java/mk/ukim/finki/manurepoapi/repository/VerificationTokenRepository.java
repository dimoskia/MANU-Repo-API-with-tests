package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    @Modifying
    @Query("delete from VerificationToken v where v.expiration < ?1")
    void deleteTokensExpirationBefore(LocalDateTime dateTime);

}
