package mk.ukim.finki.manurepoapi.jobs;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.service.AccountService;
import mk.ukim.finki.manurepoapi.service.VerificationTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final VerificationTokenService verificationTokenService;
    private final AccountService accountService;

    @Scheduled(cron = "0 0 4 * * ?")
    public void deleteExpiredTokensAndAccounts() {
        verificationTokenService.deleteExpiredTokens();
        accountService.deleteExpiredAccounts();
    }

}
