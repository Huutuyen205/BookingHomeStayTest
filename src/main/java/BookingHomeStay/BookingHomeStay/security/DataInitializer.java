package BookingHomeStay.BookingHomeStay.security;

import BookingHomeStay.BookingHomeStay.entity.Role;
import BookingHomeStay.BookingHomeStay.entity.User;
import BookingHomeStay.BookingHomeStay.repository.RoleRepository;
import BookingHomeStay.BookingHomeStay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        for (String roleName : List.of("ROLE_CUSTOMER", "ROLE_HOST", "ROLE_ADMIN", "ROLE_COLLABORATOR")) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setName(roleName);
                        return roleRepository.save(r);
                    });
        }

        if (!userRepository.existsByEmail("admin@bookinghomestay.local")) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            User admin = User.builder()
                    .fullName("System Admin")
                    .email("admin@bookinghomestay.local")
                    .password(passwordEncoder.encode("Admin@123"))
                    .roles(roles)
                    .build();
            userRepository.save(admin);
            System.out.println(">>> Da tao Admin mac dinh: admin@bookinghomestay.local / Admin@123");
        }
    }
}