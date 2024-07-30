package com.abc.mapper;

import com.abc.dto.request.UsersCreationRequest;
import com.abc.dto.request.UsersUpdateRequest;
import com.abc.dto.response.UsersResponse;
import com.abc.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

//@Mapper(componentModel = "spring")
//@Component
@Mapper(componentModel = "spring")
public interface UsersMapper {
    Users toUser(UsersCreationRequest request);
    UsersResponse toUsersResponse(Users users);
    @Mapping(target = "roles", ignore = true)
    void updateusers(@MappingTarget Users users, UsersUpdateRequest usersUpdateRequest);
}
