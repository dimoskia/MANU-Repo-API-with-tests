package mk.ukim.finki.manurepoapi.listener;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.event.OnRegistrationCompleteEvent;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import mk.ukim.finki.manurepoapi.service.VerificationTokenService;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class RegistrationListener {

    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender mailSender;

    @EventListener
    public void sendConfirmationMail(OnRegistrationCompleteEvent event) {
        Account account = (Account) event.getSource();
        VerificationToken verificationToken = verificationTokenService.createToken(account);

        String recipientAddress = account.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/accounts/confirmRegistration")
                .queryParam("token", verificationToken.getToken())
                .toUriString();

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setFrom("ukimrepository@gmail.com");
        email.setText("Please click the link below to finish the registration process:\n" + confirmationUrl);
        mailSender.send(email);
    }

}
