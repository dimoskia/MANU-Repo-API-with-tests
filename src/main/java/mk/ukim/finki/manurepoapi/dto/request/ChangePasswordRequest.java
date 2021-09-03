package mk.ukim.finki.manurepoapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.validator.PasswordMatches;
import mk.ukim.finki.manurepoapi.validator.ValidPassword;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@PasswordMatches
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest implements PasswordCredentials {

    @NotEmpty(message = "You must enter your current password")
    private String currentPassword;

    @ValidPassword
    private String password;

    private String confirmPassword;

}
