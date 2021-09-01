package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
class VerificationTokenRepositoryIntTest {

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void deleteTokensExpirationBefore_givenTableOfTokens_deletesOnlyExpiredOnes() {
        // given
        LocalDateTime boundaryValue = LocalDateTime.of(2021, 1, 1, 10, 10);

        Account account = entityManager.persist(createAccount("email1@test.com"));
        entityManager.persist(createVerificationToken(account, boundaryValue.minusMinutes(1)));

        Account account2 = entityManager.persist(createAccount("email2@test.com"));
        Long tokenId2 = entityManager.persistAndGetId(createVerificationToken(account2, boundaryValue), Long.class);

        Account account3 = entityManager.persist(createAccount("email3@test.com"));
        Long tokenId3 = entityManager.persistAndGetId(createVerificationToken(account3, boundaryValue.plusMinutes(1)), Long.class);

        // when
        verificationTokenRepository.deleteTokensExpirationBefore(boundaryValue);

        // then
        assertThat(verificationTokenRepository.findAll())
                .hasSize(2)
                .extracting(VerificationToken::getId)
                .containsExactlyInAnyOrder(tokenId2, tokenId3);
    }

    private VerificationToken createVerificationToken(Account account, LocalDateTime expiration) {
        return VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .expiration(expiration)
                .account(account)
                .build();
    }

    private Account createAccount(String email) {
        return Account.builder()
                .email(email)
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .enabled(true)
                .memberType(MemberType.CORRESPONDING)
                .department(Department.MBS)
                .build();
    }
}
