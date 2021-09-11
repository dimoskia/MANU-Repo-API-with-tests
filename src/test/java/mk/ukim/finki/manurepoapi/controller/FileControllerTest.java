package mk.ukim.finki.manurepoapi.controller;

import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.model.FileData;
import mk.ukim.finki.manurepoapi.security.MockUserDetailsService;
import mk.ukim.finki.manurepoapi.service.FileService;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static mk.ukim.finki.manurepoapi.utils.TestUtils.hasId;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FileController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(MockUserDetailsService.class)
class FileControllerTest {

    @MockBean
    FileService fileService;

    @Autowired
    MockMvc mockMvc;

    private final Long fileId = 1L;

    @Test
    void downloadFile_givenFileId_shouldDownloadFile() throws Exception {
        // given
        byte[] data = "textFileContent".getBytes();
        File file = File.builder()
                .id(fileId)
                .fileName("testFile.txt")
                .size((long) data.length)
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .fileData(new FileData(data))
                .build();

        when(fileService.downloadPublicFile(anyLong())).thenReturn(file);

        // when, then
        mockMvc.perform(get("/files/{fileId}", fileId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, data.length))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"testFile.txt\""))
                .andExpect(content().bytes(data));
        verify(fileService).downloadPublicFile(fileId);
    }

    @Nested
    class AuthenticatedAPIs {

        private final Long accountId = 10L;

        private String validUserJwt;

        @BeforeEach
        void setUp() {
            validUserJwt = String.format("Bearer %s", TestUtils.createValidUserJwt(accountId));
        }

        @Nested
        class DeleteFileFromRecord {
            @Test
            void deleteFileFromRecord_accountHasNoPermissions_statusIsForbidden() throws Exception {
                // given
                doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN))
                        .when(fileService)
                        .removeFile(any(Authentication.class), anyLong());

                // when, then
                mockMvc.perform(delete("/files/{fileId}", fileId)
                        .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                        .andExpect(status().isForbidden());

                verify(fileService).removeFile(argThat(auth -> hasId(auth, accountId)), eq(fileId));
            }

            @Test
            void deleteFileFromRecord_accountHasPermissions_fileIsDeleted() throws Exception {
                // when, then
                mockMvc.perform(delete("/files/{fileId}", fileId)
                        .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                        .andExpect(status().isNoContent());

                verify(fileService).removeFile(argThat(auth -> hasId(auth, accountId)), eq(fileId));
            }
        }

        @Test
        void addFileToRecord_givenFileAndRecordId_fileIsSaved() throws Exception {
            // given
            final Long recordId = 100L;

            String originalFilename = "grades.pdf";
            byte[] mockData = "textFileContent".getBytes();
            long fileSize = mockData.length;

            MockMultipartFile mockMultipartFile =
                    new MockMultipartFile("file", originalFilename, MediaType.APPLICATION_PDF_VALUE, mockData);

            File file = File.builder()
                    .id(fileId)
                    .fileName(originalFilename)
                    .size(fileSize)
                    .contentType(MediaType.APPLICATION_PDF_VALUE)
                    .fileData(new FileData(mockData))
                    .build();

            when(fileService.saveFileToRecord(any(MultipartFile.class), anyLong(), any(Authentication.class))).thenReturn(file);

            // when, then
            mockMvc.perform(multipart("/files/{recordId}", recordId)
                    .file(mockMultipartFile)
                    .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/files/" + fileId))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("fileName", is(originalFilename)))
                    .andExpect(jsonPath("size", is(mockData.length)))
                    .andExpect(jsonPath("contentType", is(MediaType.APPLICATION_PDF_VALUE)))
                    .andExpect(jsonPath("fileDownloadUri", is("http://localhost/files/" + fileId)));

            verify(fileService).saveFileToRecord(eq(mockMultipartFile), eq(recordId), argThat(auth -> hasId(auth, accountId)));
        }
    }

}
