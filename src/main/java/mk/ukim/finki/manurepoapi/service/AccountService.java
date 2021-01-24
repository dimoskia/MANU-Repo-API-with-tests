package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.model.Account;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AccountService {

    Account getAccount(Long accountId);

    void setProfileImage(Long accountId, MultipartFile imageFile) throws IOException;

    void deleteProfileImage(Long accountId);

    List<Account> getMultipleAccounts(List<Long> accountIds);

    boolean isEmailAvailable(String email);

}
