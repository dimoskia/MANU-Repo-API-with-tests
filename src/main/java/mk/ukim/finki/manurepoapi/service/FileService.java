package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.model.File;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    File saveFileToRecord(MultipartFile multipartFile, Long recordId, Authentication authentication) throws IOException;

    File downloadPublicFile(Long fileId);

    File getFile(Long fileId);

    void removeFile(Authentication authentication, Long fileId);

}
