package mk.ukim.finki.manurepoapi.dto;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;
import mk.ukim.finki.manurepoapi.validator.EmailAvailable;
import mk.ukim.finki.manurepoapi.validator.PasswordMatches;
import mk.ukim.finki.manurepoapi.validator.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@PasswordMatches
public class AccountRequest {

    @NotEmpty(message = "The email must not be empty")
    @Email(message = "Invalid email address")
    @EmailAvailable
    private String email;

    @ValidPassword
    private String password;

    private String confirmPassword;

    @NotEmpty(message = "You must provide first name")
    private String firstName;

    @NotEmpty(message = "You must provide last name")
    private String lastName;

    @NotNull(message = "You must provide a member type")
    private MemberType memberType;

    @NotNull(message = "You must provide a department")
    private Department department;

}
