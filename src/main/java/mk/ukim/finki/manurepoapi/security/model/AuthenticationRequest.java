package mk.ukim.finki.manurepoapi.security.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class AuthenticationRequest {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

}
