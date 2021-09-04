package mk.ukim.finki.manurepoapi.service.impl;

import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.model.FileData;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.repository.FileRepository;
import mk.ukim.finki.manurepoapi.service.RecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    FileRepository fileRepository;

    @Mock
    RecordService recordService;

    @InjectMocks
    FileServiceImpl fileService;

    @Mock
    Authentication authentication;

    @Nested
    class SaveFileToRecord {

        private final Long recordId = 1L;

        @Test
        void saveFileToRecord_userDoesNotHavePermissions_exceptionIsThrownForbiddenStatus() throws IOException {
            // given
            when(recordService.checkRecordPermissions(recordId, authentication)).thenReturn(false);

            // when, then
            assertThatThrownBy(() -> fileService.saveFileToRecord(null, recordId, authentication))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN);
        }

        @ParameterizedTest
        @CsvSource(value = {
                "text/plain, text/plain",
                ", application/octet-stream",
                "null, application/octet-stream"
        }, nullValues = "null")
        void saveFileToRecord_userHasPermissions_FileSavedAndRecordRelationEstablished(String multipartContentType, String expectedFileContentType) throws IOException {
            // given
            byte[] fileData = "file".getBytes();
            MockMultipartFile multipartFile = new MockMultipartFile("name", "originalFileName", multipartContentType, fileData);
            Record record = Record.builder().id(recordId).build();

            when(recordService.checkRecordPermissions(recordId, authentication)).thenReturn(true);
            when(recordService.getRecordRef(recordId)).thenReturn(record);

            File expectedFile = File.builder()
                    .fileName("originalFileName")
                    .size((long) fileData.length)
                    .contentType(expectedFileContentType)
                    .record(record)
                    .fileData(new FileData(fileData))
                    .build();

            when(fileRepository.save(expectedFile)).thenReturn(expectedFile);

            // when
            File actualFile = fileService.saveFileToRecord(multipartFile, recordId, authentication);

            // then
            assertThat(actualFile).isEqualTo(expectedFile);
        }
    }

    @Nested
    class DownloadPublicFile {

        private final Long fileId = 1L;
        private final Long recordId = 10L;

        private File file;

        @BeforeEach
        void setUp() {
            file = File.builder()
                    .id(fileId)
                    .record(Record.builder().id(recordId).build())
                    .build();
        }

        @Test
        void downloadPublicFile_fileDoesNotExits_exceptionIsThrown() {
            // given
            when(fileRepository.fetchFileWithData(fileId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> fileService.downloadPublicFile(fileId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("File was not found for {id=1}");
        }

        @Test
        void downloadPublicFile_associatedRecordIsNotPublic_exceptionIsThrown() {
            // given
            when(fileRepository.fetchFileWithData(fileId)).thenReturn(Optional.of(file));
            when(recordService.isRecordPublic(recordId)).thenReturn(false);

            // when, then
            assertThatThrownBy(() -> fileService.downloadPublicFile(fileId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("File was not found for {id=1}");
            verifyNoMoreInteractions(recordService);
        }

        @Test
        void downloadPublicFile_recordExistsAndIsPublic_recordIsDownloadedAndCountIncremented() {
            // given
            when(fileRepository.fetchFileWithData(fileId)).thenReturn(Optional.of(file));
            when(recordService.isRecordPublic(recordId)).thenReturn(true);

            // when
            File actualFile = fileService.downloadPublicFile(fileId);

            // then
            verify(recordService).incrementDownloads(recordId);
            assertThat(actualFile).isEqualTo(file);
        }
    }

    @Nested
    class GetFile {

        private final Long fileId = 1L;

        @Test
        void getFile_fileExists_fileFetchedAndReturned() {
            // given
            File file = File.builder().id(fileId).build();
            when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

            // when
            File actualFile = fileService.getFile(fileId);

            // then
            assertThat(actualFile).isEqualTo(file);
        }

        @Test
        void getFile_fileDoesNotExist_exceptionIsThrown() {
            // given
            when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> fileService.getFile(fileId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("File was not found for {id=1}");
        }
    }

    @Nested
    class RemoveFile {

        private final Long fileId = 1L;
        private final Long recordId = 10L;

        private File file;

        @BeforeEach
        void setUp() {
            file = File.builder()
                    .id(fileId)
                    .record(Record.builder().id(recordId).build())
                    .build();
            when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        }

        @Test
        void removeFile_userHasPermissionsForFile_deletesFile() {
            // given
            when(recordService.checkRecordPermissions(recordId, authentication)).thenReturn(true);

            // when
            fileService.removeFile(authentication, fileId);

            // then
            verify(fileRepository).delete(file);
        }

        @Test
        void removeFile_userDoesNotHavePermissionsForFile_exceptionIsThrownWithStatusForbidden() {
            // given
            when(recordService.checkRecordPermissions(recordId, authentication)).thenReturn(false);

            // when, then
            assertThatThrownBy(() -> fileService.removeFile(authentication, fileId))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN);
            verifyNoMoreInteractions(fileRepository);
        }
    }

}
