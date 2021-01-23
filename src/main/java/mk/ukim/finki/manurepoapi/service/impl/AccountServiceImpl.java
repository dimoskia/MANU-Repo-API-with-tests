package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.ProfileImage;
import mk.ukim.finki.manurepoapi.repository.AccountRepository;
import mk.ukim.finki.manurepoapi.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Account.class, accountId));
    }

    @Override
    public void setProfileImage(Long accountId, MultipartFile imageFile) throws IOException {
        Account account = getAccount(accountId);
        ProfileImage profileImage = account.getProfileImage();
        if (profileImage == null) {
            profileImage = new ProfileImage(imageFile);
        } else {
            profileImage.setData(imageFile.getBytes());
            profileImage.setContentType(imageFile.getContentType());
        }
        account.setProfileImage(profileImage);
        accountRepository.save(account);
    }

    @Override
    public void deleteProfileImage(Long accountId) {

    }
}
