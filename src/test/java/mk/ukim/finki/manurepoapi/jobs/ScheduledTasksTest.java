package mk.ukim.finki.manurepoapi.jobs;

import mk.ukim.finki.manurepoapi.service.AccountService;
import mk.ukim.finki.manurepoapi.service.VerificationTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduledTasksTest {

    @Mock
    VerificationTokenService verificationTokenService;

    @Mock
    AccountService accountService;

    @InjectMocks
    ScheduledTasks scheduledTasks;

    @Test
    void deleteExpiredTokensAndAccounts_shouldCallServiceMethodsInRightOrder() {
        // given
        InOrder inOrder = Mockito.inOrder(verificationTokenService, accountService);

        // when
        scheduledTasks.deleteExpiredTokensAndAccounts();

        // then
        inOrder.verify(verificationTokenService).deleteExpiredTokens();
        inOrder.verify(accountService).deleteExpiredAccounts();
    }

}
