package mk.ukim.finki.manurepoapi.exception;

import org.springframework.util.StringUtils;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> clazz, Long entityId) {
        super(generateMessage(clazz, entityId));
    }

    private static String generateMessage(Class<?> clazz, Long entityId) {
        String entityType = StringUtils.capitalize(clazz.getSimpleName());
        return String.format("%s was not found for {id=%d}", entityType, entityId);
    }

}
