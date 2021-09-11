package mk.ukim.finki.manurepoapi.security.service;

import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.repository.AccountRepository;
import mk.ukim.finki.manurepoapi.security.model.UserPrincipal;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    JpaUserDetailsService jpaUserDetailsService;

    private final String email = "Aleksandar.Dimoski@email.com";

    @Test
    void loadUserByUsername_userNotFound_exceptionIsThrown() {
        // given
        when(accountRepository.findByEmailAndEnabledTrue(email)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> jpaUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(String.format("User with email = '%s' not found!", email));
    }

    @Test
    void loadUserByUsername_userFound_userPrincipalReturned() {
        // given
        Account account = TestUtils.createAccount("Aleksandar", "Dimoski", true);
        when(accountRepository.findByEmailAndEnabledTrue(email)).thenReturn(Optional.of(account));

        // when
        UserDetails userDetails = jpaUserDetailsService.loadUserByUsername(email);

        // then
        assertThat(userDetails).isNotNull()
                .isInstanceOf(UserPrincipal.class)
                .matches(user -> Objects.equals(account, ((UserPrincipal) user).getAccount()));
    }
}
