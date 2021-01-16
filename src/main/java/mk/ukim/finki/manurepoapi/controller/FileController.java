package mk.ukim.finki.manurepoapi.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.FileResponse;
import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.service.FileService;
import mk.ukim.finki.manurepoapi.util.DtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    @PostMapping("/{recordId}")
    public ResponseEntity<FileResponse> addFileToRecord(@RequestParam(name = "file") MultipartFile multipartFile,
                                          @PathVariable Long recordId) throws IOException {
        File file = fileService.saveFileToRecord(multipartFile, recordId);
        FileResponse fileResponse = DtoMapper.mapFileToResponse(file);
        return ResponseEntity.created(URI.create(fileResponse.getFileDownloadUri())).body(fileResponse);
    }

    @GetMapping("/{recordId}/{fileId}")
    public void downloadFile(@PathVariable Long recordId, @PathVariable Long fileId, HttpServletResponse response) throws IOException {
        File file = fileService.getPublicFile(recordId, fileId);
        response.setContentType(file.getContentType());
        response.setContentLengthLong(file.getSize());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");
        FileCopyUtils.copy(file.getFileData().getData(), response.getOutputStream());
        response.getOutputStream().flush();
    }

    @DeleteMapping("/{recordId}/{fileId}")
    public ResponseEntity<?> deleteFileFromRecord(@PathVariable Long recordId, @PathVariable Long fileId) {
        fileService.removeFileFromRecord(recordId, fileId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
