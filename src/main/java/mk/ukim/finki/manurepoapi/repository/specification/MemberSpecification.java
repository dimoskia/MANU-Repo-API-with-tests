package mk.ukim.finki.manurepoapi.repository.specification;

import mk.ukim.finki.manurepoapi.dto.request.MembersFilter;
import mk.ukim.finki.manurepoapi.model.Account;
import org.springframework.data.jpa.domain.Specification;

import static mk.ukim.finki.manurepoapi.repository.specification.SpecificationUtils.*;

public class MemberSpecification {

    public static Specification<Account> browseMembersSpec(MembersFilter filter) {
        return Specification
                .where(searchByName(filter.getSearchTerm()))
                .and(propertyStartsWith("lastName", filter.getFirstLetter()))
                .and(propertyEquals("department", filter.getDepartment()))
                .and(propertyEquals("enabled", true));
    }

    private static Specification<Account> searchByName(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return null;
        }
        return SpecificationUtils.<Account>propertyContains("firstName", searchTerm)
                .or(propertyContains("lastName", searchTerm));
    }

}
