package com.abc.controller;

import com.abc.dto.request.ApiResponse;
import com.abc.dto.request.UsersCreationRequest;
import com.abc.dto.request.UsersUpdateRequest;
import com.abc.dto.response.UsersResponse;
import com.abc.entity.Users;
import com.abc.service.UsersService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UsersController {
    @Autowired
    private UsersService userService;

    @PostMapping
    ApiResponse<UsersResponse> createUser(@RequestBody @Valid UsersCreationRequest request){
        ApiResponse<UsersResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));

        return apiResponse;

    }

    @GetMapping("/my-info")
    ApiResponse<UsersResponse> getMyInfo() {

        return ApiResponse.<UsersResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

//@valid khai bao cho sp fw  biet rang chung ta can valitation cai object nay UsersCreationRequest dua theo field zu da dc dinh nghia trong object
    @GetMapping
    ApiResponse<List<UsersResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}",authentication.getName());
        authentication.getAuthorities()//Phương thức này trả về một tập hợp các đối tượng GrantedAuthority, đại diện cho các quyền (authorities) mà người dùng có.
                .forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        //Sử dụng một lambda expression để duyệt qua từng quyền trong tập hợp.
        //Ghi tên của quyền vào log.
        return ApiResponse.<List<UsersResponse>>builder()
                .result(userService.getUsers())
                .build();

    }
//@GetMapping
/*ApiResponse<List<UsersResponse>> getUsers() {
    return ApiResponse.<List<UsersResponse>>builder()
            .result(userService.getUsers())
            .build();
}*/

    @GetMapping("/{userId}")
    UsersResponse getUser(@PathVariable("userId") String userId){

        return userService.getUsers(userId);
    }

    @PutMapping("/{userId}")
    UsersResponse updateUser(@PathVariable String userId, @RequestBody UsersUpdateRequest request){
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return "User has been deleted";
    }
}
