package ru.intf.sasha.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.intf.sasha.service.AppVersionService;

@Component
public class VersionInterceptor implements HandlerInterceptor {

    private final AppVersionService appVersionService;

    public VersionInterceptor(AppVersionService appVersionService) {
        this.appVersionService = appVersionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Добавляем версию приложения в атрибуты запроса
        request.setAttribute("version", appVersionService.getVersion());
        return true;
    }
}