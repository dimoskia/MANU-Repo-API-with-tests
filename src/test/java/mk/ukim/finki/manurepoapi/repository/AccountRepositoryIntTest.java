package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.dto.request.MembersFilter;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import mk.ukim.finki.manurepoapi.repository.projection.MemberProjection;
import mk.ukim.finki.manurepoapi.repository.specification.MemberSpecification;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
            a - first name match
            b - last name match
            c - account enabled
        Test cases: TFT, FTT, FTF, FFT
    */
    @Test
    @DisplayName("Should return accounts where the first or last name starts with the search term disregarding casing")
    void searchByName_givenSearchTerm_returnsMemberProjectionsThatMatchByFirstOrLastName() {
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
        Predicate: a and b
        Elaboration for clauses:
            a - account is disabled
            b - verification token not present
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

    @Nested
    class BrowseMembersFiltering {

        private final Pageable pageable = PageRequest.of(0, 10);

        /*
            Logic coverage with CACC
            Predicate: a or b
            Elaboration for clauses:
                a - firstName contains searchTerm
                b - lastName contains searchTerm
            Test cases: TF, FT, FF
        */
        @Test
        void findAllWithSpecification_givenSearchTerm_shouldReturnOnlyMatchedAccountsByFirstOrLastName() {
            // given
            entityManager.persist(TestUtils.createAccount("aBcxxx", "lastName1", true));
            entityManager.persist(TestUtils.createAccount("xxxaBcyyy", "lastName2", true));
            entityManager.persist(TestUtils.createAccount("xxxaBc", "lastName3", true));

            entityManager.persist(TestUtils.createAccount("firstName1", "aBcxxx", true));
            entityManager.persist(TestUtils.createAccount("firstName2", "xxxaBcyyy", true));
            entityManager.persist(TestUtils.createAccount("firstName3", "xxxaBc", true));

            entityManager.persist(TestUtils.createAccount("firstName", "lastName", true));

            MembersFilter filter = MembersFilter.builder().searchTerm("ABC").build();

            // when
            Specification<Account> specification = MemberSpecification.browseMembersSpec(filter);
            Page<Account> accountsPage = accountRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent())
                    .hasSize(6)
                    .map(account -> String.format("%s %s", account.getFirstName(), account.getLastName()))
                    .containsExactlyInAnyOrder("aBcxxx lastName1", "xxxaBcyyy lastName2", "xxxaBc lastName3",
                            "firstName1 aBcxxx", "firstName2 xxxaBcyyy", "firstName3 xxxaBc");
        }

        @Test
        void findAllWithSpecification_givenSomeLetter_shouldReturnOnlyAccountsWhoseLastnameStartsWithLetter() {
            // given
            entityManager.persist(TestUtils.createAccount("firstName", "aLastName", true));
            entityManager.persist(TestUtils.createAccount("firstName", "someOtherLastName", true));

            MembersFilter filter = MembersFilter.builder().firstLetter("A").build();

            // when
            Specification<Account> specification = MemberSpecification.browseMembersSpec(filter);
            Page<Account> accountsPage = accountRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent()).hasSize(1);
            assertThat(accountsPage.getContent().get(0).getLastName()).isEqualTo("aLastName");
        }

        @Test
        void findAllWithSpecification_givenDepartment_shouldReturnOnlyAccountsFromDepartment() {
            // given
            entityManager.persist(TestUtils.createAccount("account1", Department.MBS));
            entityManager.persist(TestUtils.createAccount("account2", Department.LLS));

            MembersFilter filter = MembersFilter.builder().department(Department.MBS).build();

            // when
            Specification<Account> specification = MemberSpecification.browseMembersSpec(filter);
            Page<Account> accountsPage = accountRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent()).hasSize(1);
            assertThat(accountsPage.getContent().get(0).getDepartment()).isEqualTo(Department.MBS);
        }

        @Test
        void findAllWithSpecification_noFiltersApplied_shouldReturnOnlyEnabledAccounts() {
            // given
            entityManager.persist(TestUtils.createAccount("firstName1", "lastName1", true));
            entityManager.persist(TestUtils.createAccount("firstName2", "lastName2", false));

            MembersFilter emptyFilter = new MembersFilter();

            // when
            Specification<Account> specification = MemberSpecification.browseMembersSpec(emptyFilter);
            Page<Account> accountsPage = accountRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent()).hasSize(1);
            assertThat(accountsPage.getContent().get(0).getEnabled()).isTrue();
        }

    }
    
}
