package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

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
            Account authorAccount = entityManager.persist(TestUtils.createAccount("name1", "lastname1"));
            Record record = TestUtils.createRecord(Set.of(authorAccount));
            Long recordId = recordRepository.save(record).getId();

            // when
            boolean recordExists = recordRepository.existsByIdAndAuthorAccountsContaining(recordId, authorAccount);

            // then
            assertThat(recordExists).isTrue();
        }

        @Test
        void existsByIdAndAuthorAccountsContaining_accountIsNotRecordAuthor_returnsFalse() {
            // given
            Account authorAccount = entityManager.persist(TestUtils.createAccount("name1", "lastname1"));
            Record record = TestUtils.createRecord(Set.of(authorAccount));
            Long recordId = recordRepository.save(record).getId();

            Account otherAccount = entityManager.persist(TestUtils.createAccount("name2", "surname2"));

            // when
            boolean recordExists = recordRepository.existsByIdAndAuthorAccountsContaining(recordId, otherAccount);

            // then
            assertThat(recordExists).isFalse();
        }
    }

    @Test
    void incrementDownloads_downloadsCountIsOne_downloadsCountShouldBeTwo() {
        // given
        Record record = TestUtils.createRecord();
        record.setDownloadsCount(1);
        Long recordId = entityManager.persistAndGetId(record, Long.class);

        // when
        recordRepository.incrementDownloads(recordId);
        entityManager.clear();

        // then
        Record retrievedRecord = entityManager.find(Record.class, recordId);
        assertThat(retrievedRecord.getDownloadsCount()).isEqualTo(2);
    }

}
