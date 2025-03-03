package ru.intf.sasha;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.intf.sasha.model.Role;
import ru.intf.sasha.model.User;
import ru.intf.sasha.repository.RoleRepository;
import ru.intf.sasha.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Service
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
        // Создаем роли
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        roleRepository.save(userRole);

        // Создаем пользователей
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);

        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
    }
}
