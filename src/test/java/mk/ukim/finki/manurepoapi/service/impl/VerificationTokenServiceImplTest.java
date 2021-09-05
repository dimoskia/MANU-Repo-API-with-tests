package mk.ukim.finki.manurepoapi.service.impl;

import mk.ukim.finki.manurepoapi.exception.InvalidTokenException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import mk.ukim.finki.manurepoapi.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceImplTest {

    @Mock
    VerificationTokenRepository tokenRepository;

    @Mock
    Clock clock;

    @InjectMocks
    VerificationTokenServiceImpl verificationTokenService;

    private final String token = "UUIDToken";
    private final Clock fixedClock = Clock.fixed(Instant.parse("2020-02-01T10:15:00.00Z"), ZoneId.systemDefault());

    @Test
    void createToken_givenNewlyCreatedAccount_shouldCreateAndPersistToken() {
        // given
        configureMockedClock();
        Account account = Account.builder().id(1L).build();
        LocalDateTime expectedTokenExpiration = LocalDateTime.parse("2020-02-02T11:15:00.00");

        VerificationToken savedVerificationToken = new VerificationToken();
        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(savedVerificationToken);

        // when
        VerificationToken actualVerificationToken = verificationTokenService.createToken(account);

        // then
        verify(tokenRepository).save(argThat(argToken -> argToken.getAccount().equals(account)
                && argToken.getExpiration().isEqual(expectedTokenExpiration)));
        assertThat(actualVerificationToken).isEqualTo(savedVerificationToken);
    }

    @Nested
    class IsTokenExpired {

        @BeforeEach
        void setUp() {
            configureMockedClock();
        }

        @Test
        void isTokenExpired_tokenIsExpired_returnsTrue() {
            // given
            VerificationToken verificationToken = VerificationToken.builder()
                    .expiration(LocalDateTime.parse("2020-01-01T10:15:00.00"))
                    .build();

            // when
            boolean isTokenExpired = verificationTokenService.isTokenExpired(verificationToken);

            // then
            assertThat(isTokenExpired).isTrue();
        }

        @Test
        void isTokenExpired_tokenIsNotExpired_returnsFalse() {
            // given
            VerificationToken verificationToken = VerificationToken.builder()
                    .expiration(LocalDateTime.parse("2020-03-01T10:15:00.00"))
                    .build();

            // when
            boolean isTokenExpired = verificationTokenService.isTokenExpired(verificationToken);

            // then
            assertThat(isTokenExpired).isFalse();
        }
    }

    @Nested
    class GetToken {

        @Test
        void getToken_givenUUIDToken_tokenIsReturned() {
            // given
            VerificationToken verificationToken = VerificationToken.builder().token(token).build();
            when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

            // when
            VerificationToken actualVerificationToken = verificationTokenService.getToken(token);

            // then
            assertThat(actualVerificationToken).isEqualTo(verificationToken);
        }

        @Test
        void getToken_givenInvalidUUIDToken_exceptionIsThrown() {
            // given
            when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> verificationTokenService.getToken(token))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }

    @Test
    void deleteToken_givenToken_shouldCallRepositoryDeleteMethod() {
        // given
        VerificationToken verificationToken = VerificationToken.builder().token(token).build();

        // when
        verificationTokenService.deleteToken(verificationToken);

        // then
        verify(tokenRepository).delete(verificationToken);
    }

    @Test
    void deleteExpiredTokens_givenTokens_shouldCallRepositoryMethodWithCurrentInstant() {
        // given
        configureMockedClock();
        LocalDateTime fixedLocalDateTime = LocalDateTime.now(fixedClock);

        // when
        verificationTokenService.deleteExpiredTokens();

        // then
        verify(tokenRepository).deleteTokensExpirationBefore(fixedLocalDateTime);
    }

    private void configureMockedClock() {
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }
}
