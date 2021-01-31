package mk.ukim.finki.manurepoapi.jobs;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.service.AccountService;
import mk.ukim.finki.manurepoapi.service.StatisticsService;
import mk.ukim.finki.manurepoapi.service.VerificationTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final VerificationTokenService verificationTokenService;
    private final AccountService accountService;
    private final StatisticsService statisticsService;

    @Scheduled(cron = "0 0 4 * * ?")
    public void deleteExpiredTokensAndAccounts() {
        verificationTokenService.deleteExpiredTokens();
        accountService.deleteExpiredAccounts();
    }

    @Scheduled(cron = "0 0 */6 * * ?")
    public void refreshStatisticsViews() {
        statisticsService.refreshAllStats();
    }

}
