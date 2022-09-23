package com.gec.service;

import com.gec.domain.SysPermission;
import com.gec.domain.SysRole;

import java.util.List;

public interface PermissionService {
    SysRole findPermissionByUsername(String userName);

    void addPermission(SysPermission sysPermission);

    List<SysPermission> loadPermissionsByRoleId(String roleId);

    void updateRolePermission(String roleId, String[] permissionIds);
}
