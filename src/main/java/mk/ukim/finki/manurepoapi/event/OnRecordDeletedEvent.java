package mk.ukim.finki.manurepoapi.event;

import org.springframework.context.ApplicationEvent;

public class OnRecordDeletedEvent extends ApplicationEvent {

    public OnRecordDeletedEvent(Long deletedRecordId) {
        super(deletedRecordId);
    }
}
