package com.abc.controller;

import com.abc.dto.request.ApiResponse;
import com.abc.dto.request.AuthenticationRequest;
import com.abc.dto.request.IntrospectRequest;
import com.abc.dto.request.LogoutRequest;
import com.abc.dto.response.AuthenticationResponse;
import com.abc.dto.response.IntrospectResponse;
import com.abc.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
   /* @Value("${application.security.jwt.key}")
    private String Key_1 ;*/
    AuthenticationService authenticationService;
    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        AuthenticationResponse result = authenticationService.authenticate(request);
       // return ApiResponse.<AuthenticationResponse>builder()
               // .result(AuthenticationResponse.builder().build())v
               // .build();
       // System.out.println(Key_1);
            return ApiResponse.<AuthenticationResponse>builder()
                    .result(result)
                    .build();
               /*.result(AuthenticationResponse.builder()
                        .authenticated(result)
                        .build())
                .build();*/
    }

    @PostMapping("/introspect")// check token con hieu luc hay khong
    public ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        IntrospectResponse result = authenticationService.Introspect(request);
        // return ApiResponse.<AuthenticationResponse>builder()
        // .result(AuthenticationResponse.builder().build())
        // .build();
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
               /*.result(AuthenticationResponse.builder()
                        .authenticated(result)
                        .build())
                .build();*/
    }
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }
}
