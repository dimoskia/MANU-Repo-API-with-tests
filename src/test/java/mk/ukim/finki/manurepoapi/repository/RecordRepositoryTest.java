package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.dto.request.RecordsFilter;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.repository.specification.RecordSpecification;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
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
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
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

    @Nested
    class BrowseRecordsFiltering {

        private final Pageable pageable = PageRequest.of(0, 10);

        /*
            Logic coverage with CACC
            Predicate: a or b
            Elaboration for clauses:
                a - title contains searchTerm
                b - keywords contains searchTerm
            Test cases: TF, FT, FF
        */
        @Test
        void findAllWithSpecification_givenSearchTerm_shouldReturnOnlyMatchedAccountsByTitleOrKeyword() {
            // given
            entityManager.persist(TestUtils.createRecord("keyTitle", "word1, word2, word3"));
            entityManager.persist(TestUtils.createRecord("tiKEYtle", "word1, word2, word3"));
            entityManager.persist(TestUtils.createRecord("titleKey", "word1, word2, word3"));

            entityManager.persist(TestUtils.createRecord("title1", "KEYWORD, word2, word3"));
            entityManager.persist(TestUtils.createRecord("title2", "word1, KEYWORD, word3"));
            entityManager.persist(TestUtils.createRecord("title3", "word1, word2, KEYWORD"));

            entityManager.persist(TestUtils.createRecord("title", "word1, word2, word3"));

            RecordsFilter filter = RecordsFilter.builder().titleOrKeyword("KeY").build();

            // when
            Specification<Record> specification = RecordSpecification.browseRecordsSpec(filter);
            Page<Record> accountsPage = recordRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent())
                    .hasSize(6)
                    .extracting(Record::getTitle)
                    .containsExactlyInAnyOrder("keyTitle", "tiKEYtle", "titleKey", "title1", "title2", "title3");
        }

        @Test
        void findAllWithSpecification_givenRecordCollections_shouldReturnRecordsThatHaveCollectionMatched() {
            // given
            entityManager.persist(TestUtils.createRecord(Collection.ARTICLE));
            entityManager.persist(TestUtils.createRecord(Collection.BOOK));
            entityManager.persist(TestUtils.createRecord(Collection.IMAGE));

            RecordsFilter filter = RecordsFilter.builder()
                    .collections(List.of(Collection.ARTICLE, Collection.IMAGE))
                    .build();

            // when
            Specification<Record> specification = RecordSpecification.browseRecordsSpec(filter);
            Page<Record> accountsPage = recordRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent())
                    .hasSize(2)
                    .allMatch(record -> filter.getCollections().contains(record.getCollection()));
        }

        @Test
        void findAllWithSpecification_givenDepartment_shouldReturnRecordsFromDepartment() {
            // given
            entityManager.persist(TestUtils.createRecord(Department.MBS));
            entityManager.persist(TestUtils.createRecord(Department.A));

            RecordsFilter filter = RecordsFilter.builder().department(Department.A).build();

            // when
            Specification<Record> specification = RecordSpecification.browseRecordsSpec(filter);
            Page<Record> accountsPage = recordRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent())
                    .hasSize(1)
                    .allMatch(record -> record.getDepartment().equals(Department.A));
        }

        @Test
        void findAllWithSpecification_givenSubject_shouldReturnRecordsWithSpecifiedSubject() {
            // given
            entityManager.persist(TestUtils.createRecord("Subject 1"));
            entityManager.persist(TestUtils.createRecord("Subject 2"));

            RecordsFilter filter = RecordsFilter.builder().subject("Subject 1").build();

            // when
            Specification<Record> specification = RecordSpecification.browseRecordsSpec(filter);
            Page<Record> accountsPage = recordRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent())
                    .hasSize(1)
                    .allMatch(record -> record.getSubject().equals("Subject 1"));
        }

        @Test
        @Sql(statements = {
                "INSERT INTO record (title, collection, department, subject, description_or_abstract, downloads_count, date_archived, approved, private_record, authors) " +
                        "VALUES ('Public-key multi-state access', 10, 0, 'Languages and literature', 'Nam dui', 482, '2018-09-05 13:50:43', true, false, 'authors placeholder')",
                "INSERT INTO record (title, collection, department, subject, description_or_abstract, downloads_count, date_archived, approved, private_record, authors) " +
                        "VALUES ('Focused motivating service-desk', 12, 0, 'Other', 'Morbi a ipsum. Integer a nibh.', 202, '2020-06-09 15:06:40', true, false, 'authors placeholder')"
        })
        void findAllWithSpecification_givenYear_shouldExtractYearFromTimestampAndReturnOnlyMatchedRecords() {
            // given
            RecordsFilter filter = RecordsFilter.builder().year(2020).build();

            // when
            Specification<Record> specification = RecordSpecification.browseRecordsSpec(filter);
            Page<Record> accountsPage = recordRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent())
                    .hasSize(1)
                    .allMatch(record -> record.getDateArchived().getYear() == 2020);
        }

        @Test
        void findAllWithSpecification_givenAccountId_shouldReturnRecordsThatHeAuthored() {
            // given
            Account account1 = entityManager.persist(TestUtils.createAccount("firstName1", "lastName1"));
            Account account2 = entityManager.persist(TestUtils.createAccount("firstName2", "lastName2"));
            Account account3 = entityManager.persist(TestUtils.createAccount("firstName3", "lastName3"));

            entityManager.persist(TestUtils.createRecord(Set.of(account1)));
            Long recordId = entityManager.persistAndGetId(TestUtils.createRecord(Set.of(account2, account3)), Long.class);

            RecordsFilter filter = RecordsFilter.builder().authorId(account2.getId()).build();

            // when
            Specification<Record> specification = RecordSpecification.browseRecordsSpec(filter);
            Page<Record> accountsPage = recordRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent()).hasSize(1);
            assertThat(accountsPage.getContent().get(0).getId()).isEqualTo(recordId);
        }

        @Test
        void findAllWithSpecification_noFiltersSpecified_shouldReturnOnlyPublicRecords() {
            // given
            entityManager.persist(TestUtils.createRecord());
            entityManager.persist(TestUtils.createPrivateRecord());

            RecordsFilter filter = new RecordsFilter();

            // when
            Specification<Record> specification = RecordSpecification.browseRecordsSpec(filter);
            Page<Record> accountsPage = recordRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent())
                    .hasSize(1)
                    .allMatch(record -> !record.getPrivateRecord());
        }

        @Test
        void findAllWithSpecification_noFiltersSpecified_shouldReturnOnlyApprovedRecords() {
            // given
            entityManager.persist(TestUtils.createRecord());
            entityManager.persist(TestUtils.createNotApprovedRecord());

            RecordsFilter filter = new RecordsFilter();

            // when
            Specification<Record> specification = RecordSpecification.browseRecordsSpec(filter);
            Page<Record> accountsPage = recordRepository.findAll(specification, pageable);

            // then
            assertThat(accountsPage.getContent())
                    .hasSize(1)
                    .allMatch(Record::getApproved);
        }
    }

}
