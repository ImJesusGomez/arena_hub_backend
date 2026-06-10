package com.jesus_dev.arena_hub.data;

import com.jesus_dev.arena_hub.model.Role;
import com.jesus_dev.arena_hub.model.User;
import com.jesus_dev.arena_hub.model.enums.RoleName;
import com.jesus_dev.arena_hub.repository.RoleRepository;
import com.jesus_dev.arena_hub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Creamos el rol de admin
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(RoleName.ROLE_ADMIN);
                    return roleRepository.save(newRole);
                });

        // Creamos el rol de client
        Role clientRole = roleRepository.findByName(RoleName.ROLE_CLIENT)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(RoleName.ROLE_CLIENT);
                    return roleRepository.save(newRole);
                });

        // Creamos el rol de developer
        Role devRole = roleRepository.findByName(RoleName.ROLE_DEVELOPER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(RoleName.ROLE_DEVELOPER);
                    return roleRepository.save(newRole);
                });

        // Creamos el usuario de tipo admin
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            // Creamos el usuario
            User adminUser = new User();
            adminUser.setFirstName("admin");
            adminUser.setLastName("principal");
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("admin1234"));

            // Agregamos los roles
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(clientRole);
            adminRoles.add(devRole);
            adminUser.setRoles(adminRoles);

            // Lo guardamos
            userRepository.save(adminUser);
        }

        // Creamos el usuario de tipo client
        if (userRepository.findByEmail("client@gmail.com").isEmpty()) {
            // Creamos el usuario
            User clientUser = new User();
            clientUser.setFirstName("client");
            clientUser.setLastName("principal");
            clientUser.setEmail("client@gmail.com");
            clientUser.setPassword(passwordEncoder.encode("client1234"));

            // Agregamos los roles
            Set<Role> clientRoles = new HashSet<>();
            clientRoles.add(clientRole);
            clientUser.setRoles(clientRoles);

            // Lo guardamos
            userRepository.save(clientUser);
        }

        // Creamos el usuario de tipo dev
        if (userRepository.findByEmail("dev@gmail.com").isEmpty()) {
            // Creamos el usuario
            User devUser = new User();
            devUser.setFirstName("developer");
            devUser.setLastName("principal");
            devUser.setEmail("dev@gmail.com");
            devUser.setPassword(passwordEncoder.encode("developer1234"));

            // Agregamos los roles
            Set<Role> devRoles = new HashSet<>();
            devRoles.add(devRole);
            devUser.setRoles(devRoles);

            // Lo guardamos
            userRepository.save(devUser);
        }
    }
}
