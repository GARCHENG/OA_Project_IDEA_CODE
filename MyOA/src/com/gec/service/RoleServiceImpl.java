package com.gec.service;

import com.gec.domain.*;
import com.gec.mapper.SysRoleMapper;
import com.gec.mapper.SysRolePermissionMapper;
import com.gec.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public List<SysRole> findAllRoles() {
        List<SysRole> roleList = sysRoleMapper.selectByExample(null);
        return roleList;
    }

    @Override
    public void saveRoleAndPermissions(String[] permissionIds, String name) {
        //添加角色
        SysRole sysRole = new SysRole();
        sysRole.setAvailable("1");
        sysRole.setName(name);
        String rid = UUID.randomUUID().toString();
        sysRole.setId(rid);
        sysRoleMapper.insert(sysRole);

        //处理角色与相关权限的关系
        for (String permissionId : permissionIds) {
            SysRolePermission sysRolePermission = new SysRolePermission();
            String rpid = UUID.randomUUID().toString();
            sysRolePermission.setId(rpid);
            sysRolePermission.setSysRoleId(rid);
            sysRolePermission.setSysPermissionId(permissionId);
            sysRolePermissionMapper.insert(sysRolePermission);
        }

    }

    @Override
    public SysRole findRoleByUserID(String username) {
        SysUserRoleExample ure = new SysUserRoleExample();
        SysUserRoleExample.Criteria criteria = ure.createCriteria();
        criteria.andSysUserIdEqualTo(username);
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectByExample(ure);
        if (sysUserRoles!=null&&sysUserRoles.size()>0){
            String sysRoleId = sysUserRoles.get(0).getSysRoleId();
            SysRole sysRole = sysRoleMapper.selectByPrimaryKey(sysRoleId);
            return sysRole;
        }
        return null;
    }

    @Override
    public void delRole(String roleId) {
        sysRoleMapper.deleteByPrimaryKey(roleId);
        System.out.println("删除成功!");
    }
}
