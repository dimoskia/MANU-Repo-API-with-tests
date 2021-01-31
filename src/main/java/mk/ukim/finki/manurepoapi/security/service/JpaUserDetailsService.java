package mk.ukim.finki.manurepoapi.security.service;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.repository.AccountRepository;
import mk.ukim.finki.manurepoapi.security.model.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return accountRepository.findByEmailAndEnabledTrue(email)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email = '%s' not found!", email)));
    }

}
