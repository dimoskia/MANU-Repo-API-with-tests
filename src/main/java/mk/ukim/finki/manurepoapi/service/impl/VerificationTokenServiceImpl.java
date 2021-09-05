package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.exception.InvalidTokenException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import mk.ukim.finki.manurepoapi.repository.VerificationTokenRepository;
import mk.ukim.finki.manurepoapi.service.VerificationTokenService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;
    private final Clock clock;

    @Override
    public VerificationToken createToken(Account account) {
        final int TOKEN_DURATION = 24;
        VerificationToken verificationToken = buildVerificationToken(account, TOKEN_DURATION);
        return tokenRepository.save(verificationToken);
    }

    private VerificationToken buildVerificationToken(Account account, int durationInHours) {
        return VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .account(account)
                .expiration(LocalDateTime.now(clock).plusHours(durationInHours))
                .build();
    }

    @Override
    public boolean isTokenExpired(VerificationToken token) {
        return token.getExpiration().isBefore(LocalDateTime.now(clock));
    }

    @Override
    public VerificationToken getToken(String token) {
        return tokenRepository.findByToken(token).orElseThrow(InvalidTokenException::new);
    }

    @Override
    public void deleteToken(VerificationToken verificationToken) {
        tokenRepository.delete(verificationToken);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        tokenRepository.deleteTokensExpirationBefore(LocalDateTime.now(clock));
    }

}
