package mk.ukim.finki.manurepoapi.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @PutMapping("/{accountId}/profileImage")
    public ResponseEntity<?> setProfileImage(@PathVariable Long accountId, @RequestParam MultipartFile imageFile) throws IOException {
        if (imageFile.getContentType() == null || !imageFile.getContentType().startsWith("image")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No image file present in the request");
        }
        accountService.setProfileImage(accountId, imageFile);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
