package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    File saveFileToRecord(MultipartFile multipartFile, Long recordId) throws IOException;

    File getPublicFile(Long recordId, Long fileId);

}
