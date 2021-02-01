package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.dto.request.AccountRequest;
import mk.ukim.finki.manurepoapi.dto.request.EditAccountRequest;
import mk.ukim.finki.manurepoapi.model.Account;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AccountService {

    Account getAccount(Long accountId);

    Account getAccount(Authentication authentication);

    Account setProfileImage(Authentication authentication, MultipartFile imageFile) throws IOException;

    void deleteProfileImage(Authentication authentication);

    List<Account> getMultipleAccounts(List<Long> accountIds);

    boolean isEmailAvailable(String email);

    Account createAccount(AccountRequest accountRequest);

    void confirmRegistration(String verificationToken);

    void deleteExpiredAccounts();

    Account editPersonalInfo(Authentication authentication, EditAccountRequest accountRequest);

}
