package com.abc.mapper;

import com.abc.dto.request.RoleRequest;
import com.abc.dto.response.RoleResponse;
import com.abc.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
