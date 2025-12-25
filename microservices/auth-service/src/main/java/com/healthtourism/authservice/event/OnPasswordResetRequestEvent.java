package com.healthtourism.authservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a user requests password reset
 * 
 * Used for async email sending with IP and User-Agent tracking.
 */
@Getter
public class OnPasswordResetRequestEvent extends ApplicationEvent {
    
    private final Long userId;
    private final String email;
    private final String resetToken;
    private final String clientIp;
    private final String userAgent;
    
    public OnPasswordResetRequestEvent(Long userId, String email, String resetToken, 
                                       String clientIp, String userAgent) {
        super(userId);
        this.userId = userId;
        this.email = email;
        this.resetToken = resetToken;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
    }
}

