package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.model.FileData;
import mk.ukim.finki.manurepoapi.model.Record;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
class FileRepositoryIntTest {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @Sql(statements = "INSERT INTO record (id, title, collection, department, subject, description_or_abstract, keywords, language, num_pages, " +
            "publication_date, publication_status, downloads_count, date_archived, approved, private_record, authors) VALUES (1, 'Fundamental " +
            "foreground forecast', 13, 0, 'Other', 'Nulla justo. Aliquam quis turpis eget elit sodales scelerisque.', 'function', NULL, 191, " +
            "'2013-11-21', 0, 582, '2019-03-16 10:16:30', false, false, 'authors placeholder')")
    void fetchFileWithData_persistNewFile_shouldCascadePersistFileDataAndProperlyPopulateForeignKeys() {
        // given
        Record record = entityManager.find(Record.class, 1L);
        File file = File.builder()
                .fileName("fileName")
                .size(123L)
                .contentType("text/plain")
                .record(record)
                .fileData(new FileData("someFileData".getBytes()))
                .build();

        // when
        Long fileId = fileRepository.save(file).getId();

        // then
        File retrievedFile = fileRepository.fetchFileWithData(fileId).orElse(null);
        assertThat(retrievedFile)
                .isNotNull()
                .extracting(File::getRecord, File::getFileData)
                .doesNotContainNull();
        assertThat(retrievedFile)
                .extracting(f -> f.getRecord().getId(), f -> f.getFileData().getId())
                .containsExactly(record.getId(), retrievedFile.getId());
    }

}
