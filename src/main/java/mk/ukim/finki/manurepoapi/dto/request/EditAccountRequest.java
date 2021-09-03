package mk.ukim.finki.manurepoapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.AcademicDegree;
import mk.ukim.finki.manurepoapi.enums.AcademicRank;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditAccountRequest {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;

    @NotEmpty(message = "You must provide first name")
    private String firstName;

    @NotEmpty(message = "You must provide last name")
    private String lastName;

    @NotNull(message = "You must provide a member type")
    private MemberType memberType;

    @NotNull(message = "You must provide a department")
    private Department department;

    private AcademicDegree academicDegree;

    private AcademicRank academicRank;

    private String shortBio;

    private String phoneNumber;

    private String workplace;

}
