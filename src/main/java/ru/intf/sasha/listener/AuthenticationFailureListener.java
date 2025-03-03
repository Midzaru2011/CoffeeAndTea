package ru.intf.sasha.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFailureListener.class);

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        // Получаем имя пользователя из неудачной попытки входа
        String username = getUsernameFromEvent(event);

        // Логируем информацию о неудачной попытке входа
        logger.warn("Неудачная попытка входа для пользователя: {}", username);
    }

    private String getUsernameFromEvent(AuthenticationFailureBadCredentialsEvent event) {
        try {
            // Попытка получить имя пользователя из объекта аутентификации
            return (String) event.getAuthentication().getPrincipal();
        } catch (Exception e) {
            return "Неизвестный пользователь";
        }
    }

}
