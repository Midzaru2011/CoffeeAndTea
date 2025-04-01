package ru.intf.sasha.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.intf.sasha.interceptor.VersionInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final VersionInterceptor versionInterceptor;

    // Внедряем интерцептор через конструктор
    public WebConfig(VersionInterceptor versionInterceptor) {
        this.versionInterceptor = versionInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(365 * 24 * 60 * 60); // Кэширование на 1 год
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Регистрируем интерцептор для всех запросов
        registry.addInterceptor(versionInterceptor);
    }
}