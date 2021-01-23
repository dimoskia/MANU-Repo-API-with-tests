package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.model.Account;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AccountService {

    Account getAccount(Long accountId);

    void setProfileImage(Long accountId, MultipartFile imageFile) throws IOException;

    void deleteProfileImage(Long accountId);

}
