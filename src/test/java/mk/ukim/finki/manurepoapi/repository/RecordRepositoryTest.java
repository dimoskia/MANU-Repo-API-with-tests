package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;
import mk.ukim.finki.manurepoapi.enums.PublicationStatus;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.Record;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
class RecordRepositoryTest {

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    class ExistsByIdAndAuthorAccountsContaining {

        @Test
        void existsByIdAndAuthorAccountsContaining_accountIsRecordAuthor_returnsTrue() {
            // given
            Account authorAccount = entityManager.persist(createAccount("name1", "lastname1"));
            Record record = createRecord(Set.of(authorAccount));
            Long recordId = recordRepository.save(record).getId();

            // when
            boolean recordExists = recordRepository.existsByIdAndAuthorAccountsContaining(recordId, authorAccount);

            // then
            assertThat(recordExists).isTrue();
        }

        @Test
        void existsByIdAndAuthorAccountsContaining_accountIsNotRecordAuthor_returnsFalse() {
            // given
            Account authorAccount = entityManager.persist(createAccount("name1", "lastname1"));
            Record record = createRecord(Set.of(authorAccount));
            Long recordId = recordRepository.save(record).getId();

            Account otherAccount = entityManager.persist(createAccount("name2", "surname2"));

            // when
            boolean recordExists = recordRepository.existsByIdAndAuthorAccountsContaining(recordId, otherAccount);

            // then
            assertThat(recordExists).isFalse();
        }
    }

    @Test
    void incrementDownloads_downloadsCountIsOne_downloadsCountShouldBeTwo() {
        // given
        Record record = createRecord();
        record.setDownloadsCount(1);
        Long recordId = entityManager.persistAndGetId(record, Long.class);

        // when
        recordRepository.incrementDownloads(recordId);
        entityManager.clear();

        // then
        Record retrievedRecord = entityManager.find(Record.class, recordId);
        assertThat(retrievedRecord.getDownloadsCount()).isEqualTo(2);
    }

    private Account createAccount(String firstName, String lastName) {
        return Account.builder()
                .email(String.format("%s.%s@email.com", firstName, lastName))
                .password("password")
                .firstName(firstName)
                .lastName(lastName)
                .memberType(MemberType.CORRESPONDING)
                .department(Department.MBS)
                .build();
    }

    private Record createRecord(Set<Account> authorAccounts) {
        Record record = createRecord();
        record.setAuthorAccounts(authorAccounts);
        return record;
    }

    private Record createRecord() {
        return Record.builder()
                .title("Organic client-driven secured line")
                .collection(Collection.ARTICLE)
                .department(Department.MBS)
                .subject("Other")
                .descriptionOrAbstract("Vestibulum rutrum rutrum neque.")
                .keywords("spring, testing, mockito")
                .language("English")
                .numPages(123)
                .publicationDate(LocalDate.parse("2015-05-12"))
                .publicationStatus(PublicationStatus.PUBLISHED)
                .downloadsCount(10)
                .dateArchived(LocalDateTime.of(2021, 1, 1, 10, 10))
                .approved(true)
                .privateRecord(false)
                .authors("authorsPlaceholder")
                .build();
    }

}
