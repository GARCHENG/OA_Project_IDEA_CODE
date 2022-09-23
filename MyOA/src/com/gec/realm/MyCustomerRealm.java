package com.gec.realm;

import com.gec.domain.*;
import com.gec.service.EmpService;
import com.gec.service.MenuService;
import com.gec.service.PermissionService;
import com.gec.service.RoleService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyCustomerRealm extends AuthorizingRealm {
    @Autowired
    private EmpService empService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RoleService roleService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        ActiveUser activeUser = (ActiveUser) principalCollection.getPrimaryPrincipal();
        String username = activeUser.getUsername();
        SysRole role = roleService.findRoleByUserID(username);
        SimpleAuthorizationInfo simpleAuthorizationInfo = null;
        if (role != null) {
            List<SysPermission> sysPermissionList = permissionService.loadPermissionsByRoleId(role.getId());

            List<String> permissions = new ArrayList<>();
            if (sysPermissionList != null) {
                for (SysPermission sysPermission : sysPermissionList) {
                    permissions.add(sysPermission.getPercode());
                }
                simpleAuthorizationInfo = new SimpleAuthorizationInfo();
                simpleAuthorizationInfo.addStringPermissions(permissions);

            }
        }

        return simpleAuthorizationInfo;
    }


    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        String name = (String) authenticationToken.getPrincipal();
        Employee employee = empService.findEmpByName(name);
        if (employee != null) {
            ActiveUser activeUser = new ActiveUser();
            activeUser.setManagerId(employee.getManagerId());
            activeUser.setUserid(employee.getId() + "");
            activeUser.setUsername(employee.getName());
            List<SysPermission> sysmenuList = menuService.findSysPermissionWhereTypeIsMenu();
            List<MenuTree> menuTreeList =new ArrayList<>();
            if (sysmenuList != null) {

                for (SysPermission sysPermission : sysmenuList) {
                    MenuTree menuTree = new MenuTree();
                    menuTree.setId(Integer.parseInt(sysPermission.getId()+""));
                    menuTree.setName(sysPermission.getName());
                    List<SysPermission> childrens = menuService.findMenuChildrensByParentID(sysPermission.getId());
                    menuTree.setChildren(childrens);
                    menuTreeList.add(menuTree);
                }
            }
            activeUser.setMenuTree(menuTreeList);
            SimpleAuthenticationInfo myRealm
                    = new SimpleAuthenticationInfo(activeUser, employee.getPassword(), ByteSource.Util.bytes(employee.getSalt()), "MyRealm");
            return myRealm;

        }

        return null;
    }
}
