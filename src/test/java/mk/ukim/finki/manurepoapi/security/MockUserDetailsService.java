package mk.ukim.finki.manurepoapi.security;

import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;
import mk.ukim.finki.manurepoapi.enums.Role;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.security.model.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MockUserDetailsService implements UserDetailsService {

    private final Account account = Account.builder()
            .email("aleksandar.dimoski@students.finki.ukim.mk")
            .password("password")
            .role(Role.ROLE_USER)
            .enabled(true)
            .firstName("Aleksandar")
            .lastName("Dimoski")
            .memberType(MemberType.CORRESPONDING)
            .department(Department.MBS)
            .build();

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return new UserPrincipal(account);
    }

}
