package mk.ukim.finki.manurepoapi.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class CheckEmailRequest {

    @NotEmpty(message = "You must provide an email address")
    @Email(message = "Email must be of valid format")
    private String email;

}
