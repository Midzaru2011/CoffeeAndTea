package dfsf;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.intf.sasha.model.Role;
import ru.intf.sasha.model.User;
import ru.intf.sasha.repository.RoleRepository;
import ru.intf.sasha.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Создаем роли
            if (roleRepository.existsByName("ROLE_ADMIN")) {
                Role adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);
            }

            if (roleRepository.existsByName("ROLE_USER")) {
                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);
            }

            // Создаем пользователей
            if (userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRoles(Set.of(roleRepository.findByName("ROLE_ADMIN").orElseThrow()));
                userRepository.save(admin);
            }

            if (userRepository.existsByUsername("user")) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("password"));
                user.setRoles(Set.of(roleRepository.findByName("ROLE_USER").orElseThrow()));
                userRepository.save(user);
            }
            System.out.println("Данные успешно загружены в БД!");
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
            e.printStackTrace(); // Выводим подробную информацию об ошибке
        }
    }
}
