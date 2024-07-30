package com.abc.mapper;

import com.abc.dto.request.PermissionRequest;
import com.abc.dto.response.PermissionResponse;
import com.abc.entity.Permission;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}