package mk.ukim.finki.manurepoapi.security;

import mk.ukim.finki.manurepoapi.enums.Role;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.security.model.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MockUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Account account = Account.builder()
                .id(1L)
                .firstName("Aleksandar")
                .lastName("Dimoski")
                .email("aleksandar.dimoski@students.finki.ukim.mk")
                .password(bCryptPasswordEncoder.encode("correctPassword"))
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();
        return new UserPrincipal(account);
    }

}
