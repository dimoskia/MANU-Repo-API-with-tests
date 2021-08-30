package mk.ukim.finki.manurepoapi.security.model;

import lombok.NoArgsConstructor;
import mk.ukim.finki.manurepoapi.enums.Role;
import mk.ukim.finki.manurepoapi.model.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
public class UserPrincipal implements UserDetails {

    private Account account;

    public UserPrincipal(Account account) {
        this.account = account;
    }

    public UserPrincipal(Long accountId, Role role) {
        this.account = Account.builder()
                .id(accountId)
                .role(role)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = account.getRole().toString();
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return account.getEnabled();
    }

    public Role getRole() {
        return account.getRole();
    }

    public Long getAccountId() {
        return account.getId();
    }

    public Account getAccount() {
        return account;
    }

}
