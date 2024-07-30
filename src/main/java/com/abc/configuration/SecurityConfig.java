//Đoạn mã này định cấu hình bảo mật cho một ứng dụng Spring Boot sử dụng
// JWT (JSON Web Token) để xác thực và ủy quyền

package com.abc.configuration;

import com.abc.enums.Role;
import jakarta.validation.Valid;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
//Cho biết đây là một lớp cấu hình Spring.
@Configuration
// Bật cấu hình bảo mật web của Spring Security.
@EnableWebSecurity
//Bật bảo mật ở cấp phương thức.
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${application.security.jwt.signerKey}")
    @NonFinal
    private String signerKey ="r5vzbhqhmf9asnu1edzdh0bibutew513r1ty84g4y866bwveo21pyco63mamqabu";
    //@Value("${application.security.jwt.key}")
    //protected String key;
    @Autowired
    CustomJwtDecoder customJwtDecoder;
    private final String[] PUBLIC_ENDPOINTS = {
            "/users", "/auth/token", "/auth/introspect", "/auth/logout", "/auth/refresh"
    };
    //@Bean// spring se dua vao application context
    /*public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        //.requestMatchers(HttpMethod.GET,"/users")
                        //.hasRole(Role.ADMIN.name())// dung hasrole thi k can ROLE_
                        //.hasAnyAuthority("ROLE_ADMIN")
                        //.hasRole("ADMIN")
                .anyRequest().authenticated());



        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        //.authenticationEntryPoint(new JwtAuthenticationEntryPoint())// khi authentication fail thi dieu huong userrs di dau

        );//Sử dụng jwtDecoder() để giải mã JWT.

        httpSecurity.csrf(AbstractHttpConfigurer::disable);//tắt tính năng bảo vệ chống lại CSRF trong cấu hình bảo mật của ứng dụng.
//Vô hiệu hóa bảo vệ CSRF
        return httpSecurity.build();
    }*/
    @Bean
    //SecurityFilterChain filterChain(HttpSecurity httpSecurity): Cấu hình chuỗi bộ lọc bảo mật.
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //authorizeHttpRequests: Định cấu hình quyền truy cập cho các yêu cầu HTTP.
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS)
                .permitAll()
                .anyRequest()
                .authenticated());
// oauth2ResourceServer: Cấu hình máy chủ tài nguyên OAuth2 sử dụng JWT
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        //.decoder(jwtDecoder())
                        .decoder(customJwtDecoder)//Sử dụng CustomJwtDecoder để giải mã JWT.
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }
    // cấu hình một JwtDecoder sử dụng Nimbus-JOSE-JWT library để giải mã các JWT trong ứng dụng Spring Boot
    @Bean
    JwtDecoder jwtDecoder(){
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(),"HS512");//Tạo một SecretKeySpec
        // sử dụng khóa bí mật (secrecKey) và thuật toán HMAC-SHA512.
        //giải mã (decode) các JSON Web Token (JWT) dựa trên một khóa bí mật (secret key) và thuật toán HMAC-SHA512.
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
    /*@Bean
    PasswordEncoder passwordEncoder(){
       return new BCryptPasswordEncoder();
    }
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }*/
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
    //JwtAuthenticationConverter jwtAuthenticationConverter(): Bean JwtAuthenticationConverter để chuyển đổi xác thực JWT.
    //JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter():
    // Tạo JwtGrantedAuthoritiesConverter để chuyển đổi quyền hạn từ JWT.
    //jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""): Đặt tiền tố cho quyền hạn.
    //jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter):
    // Thiết lập bộ chuyển đổi quyền hạn cho JwtAuthenticationConverter.

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}
//httpSecurity.oauth2ResourceServer:
//
//Cấu hình máy chủ tài nguyên OAuth2.
//oauth2.jwt cấu hình việc sử dụng JWT cho xác thực.
//jwtConfigurer.decoder(customJwtDecoder):
//
//decoder được sử dụng để thiết lập một JwtDecoder tùy chỉnh để giải mã các token JWT.
//Ở đây, customJwtDecoder được truyền vào.
//Tại sao customJwtDecoder được chấp nhận:
//Bean được quản lý bởi Spring:
//
//@Component trên lớp CustomJwtDecoder cho phép Spring quản lý nó như một bean.
//Khi Spring Boot khởi động, nó tự động quét các lớp có chú thích như @Component và đăng ký
// chúng như các bean trong ngữ cảnh ứng dụng.
//Triển khai giao diện JwtDecoder:
//
//CustomJwtDecoder triển khai giao diện JwtDecoder.
//Khi Spring Security cần một bộ giải mã JWT, nó chỉ cần một bean nào đó triển khai giao diện này.
//customJwtDecoder phù hợp với yêu cầu này, vì vậy nó được chấp nhận.
//Cấu hình tự động của Spring Security:
//
//Spring Security có cơ chế cấu hình tự động và dễ dàng tích hợp với các bean do Spring quản lý.
//Khi httpSecurity.oauth2ResourceServer được cấu hình, nó sẽ tìm kiếm các bean thích hợp trong ngữ cảnh ứng dụng.
//Tóm lại:
//Trong cấu hình bảo mật của Spring, khi bạn cung cấp customJwtDecoder cho phương thức decoder,
// Spring Security hiểu rằng bạn đang cung cấp một JwtDecoder tùy chỉnh vì customJwtDecoder là một
// bean được quản lý bởi Spring và triển khai giao diện JwtDecoder. Điều này cho phép Spring
// Security sử dụng customJwtDecoder để giải mã các JWT được gửi đến máy chủ tài nguyên




//JwtAuthenticationConverter trong cấu hình Spring Security được sử dụng để chuyển đổi JWT
// (JSON Web Token) thành thông tin xác thực được sử dụng bởi Spring Security để xác thực người
// dùng và phân quyền trong ứng dụng. Chi tiết các phương thức của JwtAuthenticationConverter
// giúp xử lý các thông tin từ JWT như sau:
//
//Các phương thức chính:
//setJwtGrantedAuthoritiesConverter: Đặt bộ chuyển đổi để chuyển đổi các tên quyền từ JWT thành
// các đối tượng GrantedAuthority. Mặc định, Spring Security sử dụng JwtGrantedAuthoritiesConverter
// để thực hiện việc này.
//Chức năng:
//Khi một JWT được gửi đến và xác thực thành công, JwtAuthenticationConverter sẽ được sử dụng
// để trích xuất thông tin từ JWT. Thông tin này bao gồm các quyền (authorities), thông tin người
// dùng (username, subject, ...) và bất kỳ thông tin xác thực nào khác mà bạn đã định nghĩa trong JWT.


