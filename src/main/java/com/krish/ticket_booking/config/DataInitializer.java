package com.krish.ticket_booking.config;

import com.krish.ticket_booking.entity.User;
import com.krish.ticket_booking.entity.enums.RoleEnum;
import com.krish.ticket_booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByEmail("admin@primeseats.com").isEmpty()) {
                User admin = new User();
                admin.setName("Super Admin");
                admin.setEmail("admin@primeseats.com");
                admin.setPassword(encoder.encode("Abc@123")); // strong password in real app
                admin.setRole(RoleEnum.ADMIN);

                userRepository.save(admin);
                System.out.println("✅ Default admin created: admin@system.com / admin123");
            }
        };
    }
}
