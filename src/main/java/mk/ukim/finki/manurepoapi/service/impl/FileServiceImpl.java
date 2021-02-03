package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.model.FileData;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.repository.FileRepository;
import mk.ukim.finki.manurepoapi.service.FileService;
import mk.ukim.finki.manurepoapi.service.RecordService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final RecordService recordService;

    @Override
    public File saveFileToRecord(MultipartFile multipartFile, Long recordId) throws IOException {
        Optional<String> contentTypeOptional = Optional.ofNullable(multipartFile.getContentType());
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
    public File getPublicFile(Long fileId) {
        File file = getFile(fileId);
        if (!recordService.isRecordPublic(file.getRecord().getId())) {
            throw new EntityNotFoundException(File.class, fileId);
        }
        return file;
    }

    @Override
    public File getFile(Long fileId) {
        return fileRepository.fetchFileWithData(fileId).orElseThrow(() -> new EntityNotFoundException(File.class, fileId));
    }

    @Override
    public void removeFile(Long fileId) {
        try {
            fileRepository.deleteById(fileId);
        } catch (EmptyResultDataAccessException ex) {
            throw new EntityNotFoundException(File.class, fileId);
        }
    }

}
