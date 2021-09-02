package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import mk.ukim.finki.manurepoapi.repository.projection.MemberProjection;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
class AccountRepositoryIntTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TestEntityManager entityManager;

    /*
        Logic coverage with CACC
        Predicate: (a or b) and c
        Elaboration for clauses:
        a - first name match    b - last name match     c - account enabled
        Test cases: TFT, FTT, FTF, FFT
    */
    @Test
    @DisplayName("Should return accounts where the first or last name starts with the search term disregarding casing")
    void searchByName_searchTermMatchesName_returnsMemberProjections() {
        // given
        List<Account> accounts = List.of(
                TestUtils.createAccount("Aleksandar", "Dimoski", true),
                TestUtils.createAccount("Stefan", "Aleksovski", true),
                TestUtils.createAccount("Dragana", "Aleksov", false),
                TestUtils.createAccount("Kostadin", "Ljatkoski", true),
                TestUtils.createAccount("Aleksandar", "Milanov", true)
        );
        accounts.forEach(account -> entityManager.persist(account));

        String searchTerm = "aLeK";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<MemberProjection> searchResults = accountRepository.searchByName(searchTerm, pageable);

        // then
        assertThat(searchResults)
                .hasSize(3)
                .extracting(MemberProjection::getFullName)
                .containsExactly("Aleksandar Dimoski", "Aleksandar Milanov", "Stefan Aleksovski");
    }

    @Test
    void enableAccount_givenAccountId_shouldSetEnabledFlagToTrue() {
        // given
        Account disabledAccount = TestUtils.createAccount(false);
        Long accountId = entityManager.persistAndGetId(disabledAccount, Long.class);

        // when
        accountRepository.enableAccount(accountId);
        entityManager.clear();

        // then
        Account retrievedAccount = entityManager.find(Account.class, accountId);
        assertThat(retrievedAccount.getEnabled()).isTrue();
    }

    /*
        Logic coverage with CACC
        Predicate: (a and b)
        Elaboration for clauses:
        a - account is disabled    b - verification token not present
        Test cases: TT, TF, FT
    */
    @Test
    void deleteExpiredAccounts_givenAccountsAndTokens_shouldDeleteAccountsWithoutTokens() {
        // given
        entityManager.persist(TestUtils.createAccount("name1", "surname1", false));

        Account disabledAccount = TestUtils.createAccount("name2", "surname2", false);
        Long disabledAccountId = entityManager.persistAndGetId(disabledAccount, Long.class);
        entityManager.persist(new VerificationToken(disabledAccount, 24));

        Account enabledAccount = TestUtils.createAccount("name3", "surname2", true);
        Long enabledAccountId = entityManager.persistAndGetId(enabledAccount, Long.class);
        entityManager.persist(new VerificationToken(enabledAccount, 24));

        // when
        accountRepository.deleteExpiredAccounts();

        // then
        assertThat(accountRepository.findAll())
                .hasSize(2)
                .extracting(Account::getId)
                .containsExactlyInAnyOrder(disabledAccountId, enabledAccountId);
    }
    
}
