package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
class FileRepositoryIntTest {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void fetchFileWithData_persistNewFile_shouldCascadePersistFileDataAndProperlyPopulateForeignKeys() {
        // given
        Record record = entityManager.persist(TestUtils.createRecord());
        File file = TestUtils.createFile(record);

        // when
        Long fileId = fileRepository.save(file).getId();
        File retrievedFile = fileRepository.fetchFileWithData(fileId).orElse(null);

        // then
        assertThat(retrievedFile)
                .isNotNull()
                .extracting(File::getRecord, File::getFileData)
                .doesNotContainNull();
        assertThat(retrievedFile)
                .extracting(f -> f.getRecord().getId(), f -> f.getFileData().getId())
                .containsExactly(record.getId(), retrievedFile.getId());
    }

}
