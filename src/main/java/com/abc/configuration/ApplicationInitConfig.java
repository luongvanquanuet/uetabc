package com.abc.configuration;

import com.abc.entity.Role;
import com.abc.entity.Users;
//import com.abc.enums.Role;
import com.abc.repository.RoleRepository;
import com.abc.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    //@NonFinal
    static final String ADMIN_USER_NAME = "admin";

    //@NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(UsersRepository usersRepository, RoleRepository roleRepository) {//Phương thức applicationRunner được khai báo để trả về một đối tượng ApplicationRunner
        //ApplicationRunner là một giao diện trong Spring Boot, được thiết kế để chạy một khối mã khi
        // ứng dụng khởi động. Nó có một phương thức duy nhất là run(ApplicationArguments args), được thực thi sau khi ứng dụng khởi động.
        // RoleRepository roleRepository) {
        //log.info("Initializing application.....");
        return args -> {// args -> { ... } trong đoạn mã này triển khai phương thức run của
            // giao diện ApplicationRunner. Khi ứng dụng khởi động, đoạn mã bên trong lambda
            // sẽ được thực thi để kiểm tra và tạo tài khoản admin nếu cần thiết. Đây là một
            // cách thuận tiện và ngắn gọn để thực hiện logic khởi tạo khi ứng dụng Spring
            // Boot bắt đầu chạy
            if (usersRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
               /* roleRepository.save(Role.builder()
                        .name(PredefinedRole.USER_ROLE)
                        .description("User role")
                        .build());

                Role adminRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());
                */

                Role adminRole = roleRepository.save(Role.builder()
                        .name("ADMIN")
                        .description("Admin role")
                        .build());

                var roles = new HashSet<Role>();
                roles.add(adminRole);

                Users user = Users.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)

                        .build();

                usersRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
}
//Phương thức applicationRunner thực tế trả về một đối tượng của loại ApplicationRunner.
// Biểu thức lambda args -> { ... } là một triển khai của phương thức run trong giao diện
// ApplicationRunner, và không trực tiếp trả về một giá trị nào. Nhưng toàn bộ biểu thức
// lambda này là phần thân của đối tượng ApplicationRunner được trả về bởi phương thức
// applicationRunner
/*
@FunctionalInterface
public interface ApplicationRunner {
    void run(ApplicationArguments args) throws Exception;
}*/
//Spring IoC container
//
