package mk.ukim.finki.manurepoapi.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.dto.response.Avatar;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticationResponse {

    private String jwt;

    private Avatar avatar;

}
