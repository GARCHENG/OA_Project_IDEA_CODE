package com.gec.service;

import com.gec.domain.*;
import com.gec.mapper.*;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;


    @Override
    public SysRole findPermissionByUsername(String userName) {
        EmployeeExample ue = new EmployeeExample();
        EmployeeExample.Criteria criteria = ue.createCriteria();
        criteria.andNameEqualTo(userName);
        List<Employee> employeeList = employeeMapper.selectByExample(ue);
        if (employeeList != null) {
            Integer rid = employeeList.get(0).getRole();
            SysRole sysRole = sysRoleMapper.selectByPrimaryKey(rid + "");
            List<SysPermission> permissionListByRoleId = sysPermissionMapper.findPermissionListByRoleId(rid);
            sysRole.setPermissionList(permissionListByRoleId);
            return sysRole;
        }
        return null;
    }

    @Override
    public void addPermission(SysPermission sysPermission) {
        int i = sysPermissionMapper.insertSelective(sysPermission);
        if (i!=0){

            System.out.println();
        }
    }

    @Override
    public List<SysPermission> loadPermissionsByRoleId(String roleId) {
        SysRolePermissionExample rpe = new SysRolePermissionExample();
        SysRolePermissionExample.Criteria criteria = rpe.createCriteria();
        criteria.andSysRoleIdEqualTo(roleId);
        List<SysRolePermission> sysRolePermissions = sysRolePermissionMapper.selectByExample(rpe);

        List<Long> perIDs = new ArrayList<>();
        if (sysRolePermissions != null) {
            for (SysRolePermission sysRolePermission : sysRolePermissions) {
                perIDs.add(Long.parseLong(sysRolePermission.getSysPermissionId()));
            }
        }

        SysPermissionExample spe = new SysPermissionExample();
        SysPermissionExample.Criteria criteria1 = spe.createCriteria();
        criteria1.andIdIn(perIDs);
        List<SysPermission> sysPermissionList = sysPermissionMapper.selectByExample(spe);
        return sysPermissionList;
    }

    @Override
    public void updateRolePermission(String roleId, String[] permissionIds) {
        //删除原来角色的所有权限
        SysRolePermissionExample rpe = new SysRolePermissionExample();
        SysRolePermissionExample.Criteria criteria = rpe.createCriteria();
        criteria.andSysRoleIdEqualTo(roleId);
        sysRolePermissionMapper.deleteByExample(rpe);
        //再一次添加权限
        for (String permissionId : permissionIds) {
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setId(UUID.randomUUID().toString());
            sysRolePermission.setSysRoleId(roleId);
            sysRolePermission.setSysPermissionId(permissionId);
            sysRolePermissionMapper.insert(sysRolePermission);
        }
    }
}
