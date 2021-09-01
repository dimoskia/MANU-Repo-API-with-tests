package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import mk.ukim.finki.manurepoapi.repository.projection.MemberProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
class AccountRepositoryIntTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TestEntityManager entityManager;

    @Nested
    class FindByIdAndEnabledTrue {

        @Test
        void findByIdAndEnabledTrue_accountIsDisabled_returnsEmptyOptional() {
            // given
            Account disabledAccount = createAccount(false);
            Long accountId = entityManager.persistAndGetId(disabledAccount, Long.class);

            // when
            Optional<Account> accountOptional = accountRepository.findByIdAndEnabledTrue(accountId);

            // then
            assertThat(accountOptional).isEmpty();
        }

        @Test
        void findByIdAndEnabledTrue_accountIsEnabled_returnsAccount() {
            // given
            Account enabledAccount = createAccount(true);
            Long accountId = entityManager.persistAndGetId(enabledAccount, Long.class);

            // when
            Optional<Account> accountOptional = accountRepository.findByIdAndEnabledTrue(accountId);

            // then
            assertThat(accountOptional)
                    .isPresent()
                    .map(Account::getId)
                    .hasValue(accountId);
        }

    }

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
                createAccount("Aleksandar", "Dimoski", true),
                createAccount("Stefan", "Aleksovski", true),
                createAccount("Dragana", "Aleksov", false),
                createAccount("Kostadin", "Ljatkoski", true),
                createAccount("Aleksandar", "Milanov", true)
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
        Account disabledAccount = createAccount(false);
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
        entityManager.persist(createAccount("name1", "surname1", false));

        Account disabledAccount = createAccount("name2", "surname2", false);
        Long disabledAccountId = entityManager.persistAndGetId(disabledAccount, Long.class);
        entityManager.persist(new VerificationToken(disabledAccount, 24));

        Account enabledAccount = createAccount("name3", "surname2", true);
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

    private Account createAccount(String firstName, String lastName, boolean enabled) {
        return Account.builder()
                .email(String.format("%s.%s@email.com", firstName, lastName))
                .password("password")
                .firstName(firstName)
                .lastName(lastName)
                .enabled(enabled)
                .memberType(MemberType.CORRESPONDING)
                .department(Department.MBS)
                .build();
    }

    private Account createAccount(boolean enabled) {
        return createAccount("firstName", "lastName", enabled);
    }

}
