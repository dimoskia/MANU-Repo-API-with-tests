package mk.ukim.finki.manurepoapi.security.model;

import lombok.NoArgsConstructor;
import mk.ukim.finki.manurepoapi.model.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
public class UserPrincipal implements UserDetails {

    private String email;

    private String password;

    private String role;

    private boolean enabled;

    public UserPrincipal(Account user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole().toString();
        this.enabled = user.getEnabled();
    }

    public UserPrincipal(String email, String role) {
        this.email = email;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return enabled;
    }

    public String getRole() {
        return role;
    }

}
