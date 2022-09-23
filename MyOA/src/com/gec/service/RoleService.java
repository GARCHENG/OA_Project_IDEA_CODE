package com.gec.service;

import com.gec.domain.SysRole;

import java.util.List;

public interface RoleService {
    List<SysRole> findAllRoles();

    void saveRoleAndPermissions(String[] permissionIds, String name);

    SysRole findRoleByUserID(String username);

    void delRole(String roleId);
}
