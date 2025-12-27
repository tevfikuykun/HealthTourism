package com.healthtourism.authservice.event;

import com.healthtourism.authservice.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a new user registers
 * 
 * Used for async email sending and other post-registration tasks.
 * Prevents blocking the registration process with email sending.
 */
@Getter
public class OnUserRegistrationEvent extends ApplicationEvent {
    
    private final User user;
    private final String verificationToken;
    
    public OnUserRegistrationEvent(User user, String verificationToken) {
        super(user);
        this.user = user;
        this.verificationToken = verificationToken;
    }
}



