package mk.ukim.finki.manurepoapi.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class DeleteAccountRequest {

    @NotEmpty(message = "You must provide your password")
    private String password;

}
