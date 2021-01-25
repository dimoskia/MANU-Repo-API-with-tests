package mk.ukim.finki.manurepoapi.event;

import mk.ukim.finki.manurepoapi.model.Account;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

    public OnRegistrationCompleteEvent(Account account) {
        super(account);
    }

}
