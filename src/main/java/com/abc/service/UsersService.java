package com.abc.service;

import com.abc.dto.request.UsersCreationRequest;
import com.abc.dto.request.UsersUpdateRequest;
import com.abc.dto.response.UsersResponse;
import com.abc.entity.Users;
import com.abc.enums.Role;
import com.abc.exception.AppException;
import com.abc.exception.ErrorCode;
import com.abc.mapper.UsersMapper;
import com.abc.repository.RoleRepository;
import com.abc.repository.UsersRepository;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UsersService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    public UsersResponse createUser(UsersCreationRequest request){
        //Users user = new Users();
        if(usersRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Users users = usersMapper.toUser(request);
        /*user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());*/
        //PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        users.setPassword(passwordEncoder.encode(request.getPassword()));
       /* HashSet roles = new HashSet();
        roles.add(Role.USERS.name());
        users.setRoles(roles);*/

        //String name = contex

      //Users users1 = usersRepository.save(users);
        //return usersRepository.save(users);
        return usersMapper.toUsersResponse(usersRepository.save(users));
    }
    public UsersResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();// kho 1 request dc xac thuc thanh cong thong tin users dang nhap thanh cng dc luu vao SecurityContextHolder
        String name = context.getAuthentication().getName();
        // Phương thức này trả về đối tượng Authentication, biểu diễn thông tin xác thực của người dùng hiện đang truy cập vào hệ thống.
        Collection<? extends GrantedAuthority> authorities =context.getAuthentication().getAuthorities();
        System.out.println(authorities);
        Users user = usersRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return usersMapper.toUsersResponse(user);
    }
    @PostAuthorize("returnObject.username == authentication.name")
    public UsersResponse updateUser(String userId, UsersUpdateRequest request) {
        Users users = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

/*
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
*/
        usersMapper.updateusers(users,request);
        users.setPassword(passwordEncoder.encode(request.getPassword()));
        var roles = roleRepository.findAllById(request.getRoles());
        users.setRoles(new HashSet<>(roles));
        return usersMapper.toUsersResponse(usersRepository.save(users));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId){

        usersRepository.deleteById(userId);
    }

    /*@PreAuthorize("hasRole('ADMIN')")
    public List<UsersResponse> getUsers(){
        log.info("In method get Users");

        return usersRepository.findAll().stream().map(usersMapper::toUsersResponse).toList();
    }*/
    @PreAuthorize("hasAuthority('APPROVE_PORT')")
    //@PreAuthorize("hasRole('ADMIN')")
    public List<UsersResponse> getUsers() {
        log.info("In method get Users");
        return usersRepository.findAll().stream().map(usersMapper::toUsersResponse).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UsersResponse getUsers(String id){
        Users users = usersRepository.findById(id).get();
        System.out.println(users);
        return usersMapper.toUsersResponse(usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }
}
