package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.model.FileData;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.repository.FileRepository;
import mk.ukim.finki.manurepoapi.service.FileService;
import mk.ukim.finki.manurepoapi.service.RecordService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final RecordService recordService;

    @Override
    public File saveFileToRecord(MultipartFile multipartFile, Long recordId, Authentication authentication) throws IOException {
        if (!recordService.checkRecordPermissions(recordId, authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        Optional<String> contentTypeOptional = Optional.ofNullable(multipartFile.getContentType())
                .filter(StringUtils::hasText);
        Record record = recordService.getRecordRef(recordId);
        File file = File.builder()
                .fileName(multipartFile.getOriginalFilename())
                .size(multipartFile.getSize())
                .contentType(contentTypeOptional.orElse("application/octet-stream"))
                .record(record)
                .fileData(new FileData(multipartFile.getBytes()))
                .build();
        return fileRepository.save(file);
    }

    @Override
    public File downloadPublicFile(Long fileId) {
        File file = fileRepository.fetchFileWithData(fileId).orElseThrow(() -> new EntityNotFoundException(File.class, fileId));
        if (!recordService.isRecordPublic(file.getRecord().getId())) {
            throw new EntityNotFoundException(File.class, fileId);
        }
        recordService.incrementDownloads(file.getRecord().getId());
        return file;
    }

    @Override
    public File getFile(Long fileId) {
        return fileRepository.findById(fileId).orElseThrow(() -> new EntityNotFoundException(File.class, fileId));
    }

    @Override
    public void removeFile(Authentication authentication, Long fileId) {
        File file = getFile(fileId);
        if (!recordService.checkRecordPermissions(file.getRecord().getId(), authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        fileRepository.delete(file);
    }

}
