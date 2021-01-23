package mk.ukim.finki.manurepoapi.dto;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;

@Getter
@Setter
public class MemberCard {

    private Long id;

    private String firstName;

    private String lastName;

    private Department department;

    private MemberType memberType;

    private String imageUrl;

    public String getMemberType() {
        return memberType.getFullType();
    }

    public String getDepartment() {
        return department.getFullDepartment();
    }

}
