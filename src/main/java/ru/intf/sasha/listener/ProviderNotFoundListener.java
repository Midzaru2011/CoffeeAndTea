package ru.intf.sasha.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProviderNotFoundEvent;
import org.springframework.stereotype.Component;

@Component
public class ProviderNotFoundListener implements ApplicationListener<AuthenticationFailureProviderNotFoundEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ProviderNotFoundListener.class);

    @Override
    public void onApplicationEvent(AuthenticationFailureProviderNotFoundEvent event) {
        String username = getUsernameFromEvent(event);
        logger.warn("Пользователь {} не найден", username);
    }

    private String getUsernameFromEvent(AuthenticationFailureProviderNotFoundEvent event) {
        try {
            return (String) event.getAuthentication().getPrincipal();
        } catch (Exception e) {
            return "Неизвестный пользователь";
        }
    }
}
