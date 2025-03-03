package ru.intf.sasha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.intf.sasha.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationSuccessHandler successHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Доступ только для администраторов
                        .requestMatchers("/user/**").hasRole("USER")   // Доступ только для обычных пользователей
                        .requestMatchers("/login", "/css/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
        //        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers( "/login", "/css/**", "/images/**").permitAll() // Разрешаем доступ к главной странице и статическим ресурсам
//                        //.requestMatchers("/checkout").permitAll()
//                        .anyRequest().authenticated() // Все остальные запросы НЕ требуют авторизации
//                )
                .formLogin(form -> form
                        .loginPage("/login") // Страница для входа
                        .successHandler(successHandler)
                        .defaultSuccessUrl("/") // Перенаправление после успешного входа
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL для обработки выхода
                        .logoutSuccessUrl("/login?logout") // Перенаправление после выхода
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
//                )
//                .logout(LogoutConfigurer::permitAll
                );
//                .logout(LogoutConfigurer::permitAll // Разрешаем выход из системы
//                );
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
//    @Bean
//    public UserDetailsService userDetailsService() {
//        // Создаем пользователя в памяти
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("admin")
//                .password("admin")
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }
}
