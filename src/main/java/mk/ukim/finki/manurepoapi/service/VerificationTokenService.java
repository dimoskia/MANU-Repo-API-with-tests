package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.VerificationToken;

public interface VerificationTokenService {

    VerificationToken createToken(Account account);

    VerificationToken getToken(String token);

    boolean isTokenExpired(VerificationToken verificationToken);

    void deleteToken(VerificationToken verificationToken);

    void deleteExpiredTokens();

}
