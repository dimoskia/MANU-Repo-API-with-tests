package mk.ukim.finki.manurepoapi.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import java.text.MessageFormat;

public class SpecificationUtils {

    static <T> Specification<T> propertyEquals(String propertyName, Object value) {
        return value == null ? null : (root, query, builder) -> builder.equal(root.get(propertyName), value);
    }

    static <T> Specification<T> propertyStartsWith(String propertyName, String value) {
        if (value == null) {
            return null;
        }
        return (root, query, builder) -> {
            String likeExpression = MessageFormat.format("{0}%", value.toUpperCase());
            return builder.like(builder.upper(root.get(propertyName)), likeExpression);
        };
    }

    static <T> Specification<T> propertyContains(String propertyName, String value) {
        if (value == null) {
            return null;
        }
        return (root, query, builder) -> {
            String likeExpression = MessageFormat.format("%{0}%", value.toUpperCase());
            return builder.like(builder.upper(root.get(propertyName)), likeExpression);
        };
    }

}
