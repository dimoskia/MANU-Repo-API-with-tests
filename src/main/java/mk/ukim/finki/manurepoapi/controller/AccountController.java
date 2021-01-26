package mk.ukim.finki.manurepoapi.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.request.AccountRequest;
import mk.ukim.finki.manurepoapi.dto.response.MemberDetails;
import mk.ukim.finki.manurepoapi.event.OnRegistrationCompleteEvent;
import mk.ukim.finki.manurepoapi.exception.InvalidTokenException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.service.AccountService;
import mk.ukim.finki.manurepoapi.util.DtoMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping
    public ResponseEntity<MemberDetails> createAccount(@RequestBody @Valid AccountRequest accountRequest) {
        try {
            Account registeredAccount = accountService.createAccount(accountRequest);
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registeredAccount));
            MemberDetails memberDetails = DtoMapper.mapAccountToMemberDetails(registeredAccount);
            return new ResponseEntity<>(memberDetails, HttpStatus.CREATED);
        } catch (MailException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping("/confirmRegistration")
    public void confirmRegistration(@RequestParam(name = "token") String verificationToken,
                                    HttpServletResponse response) throws IOException {
        try {
            accountService.confirmRegistration(verificationToken);
        } catch (InvalidTokenException exception) {
            // TODO: 25-Jan-21 error page for token expired or doesn't exist
            response.sendRedirect("http://localhost:4200");
        }
        // TODO: 25-Jan-21 success page for account activation
        response.sendRedirect("http://localhost:4200");
    }

    @PutMapping("/{accountId}/profileImage")
    public ResponseEntity<?> setProfileImage(@PathVariable Long accountId, @RequestParam MultipartFile imageFile) throws IOException {
        if (imageFile.getContentType() == null || !imageFile.getContentType().startsWith("image")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No image file present in the request");
        }
        accountService.setProfileImage(accountId, imageFile);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{accountId}/profileImage")
    public ResponseEntity<?> removeProfileImage(@PathVariable Long accountId) {
        accountService.deleteProfileImage(accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/emailAvailable")
    public Boolean checkEmailAvailable(@RequestHeader String email) {
        return accountService.isEmailAvailable(email);
    }

}
