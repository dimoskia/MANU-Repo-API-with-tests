package mk.ukim.finki.manurepoapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.manurepoapi.validator.PasswordMatches;
import mk.ukim.finki.manurepoapi.validator.ValidPassword;

import javax.validation.constraints.NotEmpty;

@Data
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
