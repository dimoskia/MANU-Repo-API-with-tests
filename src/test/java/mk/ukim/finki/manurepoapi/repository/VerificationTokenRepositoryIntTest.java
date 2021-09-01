package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
class VerificationTokenRepositoryIntTest {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deleteTokensExpirationBefore_givenTableOfTokens_deletesOnlyExpiredOnes() {
        // given
        LocalDateTime boundaryValue = LocalDateTime.of(2021, 1, 1, 10, 10);

        Account account = entityManager.persist(TestUtils.createAccount("name1", "surname1"));
        entityManager.persist(TestUtils.createVerificationToken(account, boundaryValue.minusMinutes(1)));

        Account account2 = entityManager.persist(TestUtils.createAccount("name2", "surname2"));
        Long tokenId2 = entityManager.persistAndGetId(TestUtils.createVerificationToken(account2, boundaryValue), Long.class);

        Account account3 = entityManager.persist(TestUtils.createAccount("name3", "surname3"));
        Long tokenId3 = entityManager.persistAndGetId(TestUtils.createVerificationToken(account3, boundaryValue.plusMinutes(1)), Long.class);

        // when
        verificationTokenRepository.deleteTokensExpirationBefore(boundaryValue);

        // then
        assertThat(verificationTokenRepository.findAll())
                .hasSize(2)
                .extracting(VerificationToken::getId)
                .containsExactlyInAnyOrder(tokenId2, tokenId3);
    }

}
