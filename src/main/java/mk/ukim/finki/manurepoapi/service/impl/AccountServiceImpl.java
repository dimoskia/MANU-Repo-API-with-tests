package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.request.AccountRequest;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.exception.InvalidTokenException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.ProfileImage;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import mk.ukim.finki.manurepoapi.repository.AccountRepository;
import mk.ukim.finki.manurepoapi.service.AccountService;
import mk.ukim.finki.manurepoapi.service.VerificationTokenService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final VerificationTokenService verificationTokenService;

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
        Account account = getAccount(accountId);
        account.setProfileImage(null);
        accountRepository.save(account);
    }

    @Override
    public List<Account> getMultipleAccounts(List<Long> accountIds) {
        return accountRepository.findAllById(accountIds);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !accountRepository.existsByEmail(email);
    }

    @Override
    public Account createAccount(AccountRequest accountRequest) {
        Account account = new Account();
        BeanUtils.copyProperties(accountRequest, account);
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void confirmRegistration(String token) {
        VerificationToken verificationToken = verificationTokenService.getToken(token);
        if (verificationTokenService.isTokenExpired(verificationToken)) {
            throw new InvalidTokenException();
        }
        Long accountId = verificationToken.getAccount().getId();
        accountRepository.enableAccount(accountId);
        verificationTokenService.deleteToken(verificationToken);
    }

}
