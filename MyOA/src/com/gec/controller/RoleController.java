package com.gec.controller;

import com.gec.domain.MenuTree;
import com.gec.domain.SysPermission;
import com.gec.domain.SysRole;
import com.gec.domain.TreeMenu;
import com.gec.mapper.SysRoleMapper;
import com.gec.service.MenuService;
import com.gec.service.PermissionService;
import com.gec.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RoleController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RoleService roleService;

    //角色添加
    @RequestMapping("/toAddRole")
    public String toAddRole(Model model){

        List<MenuTree> treeMenuList = new ArrayList<>();
        List<SysPermission> menu = menuService.findSysPermissionWhereTypeIsMenu();
        if (menu != null) {
            for (SysPermission sysPermission : menu) {
                MenuTree menuTree = new MenuTree();
                menuTree.setId(Integer.parseInt(sysPermission.getId()+""));
                menuTree.setName(sysPermission.getName());

                List<SysPermission> menuChildrens = menuService.findMenuChildrensByParentIDLikePermission(sysPermission.getId());
                menuTree.setChildren(menuChildrens);
                treeMenuList.add(menuTree);
            }
        }
        model.addAttribute("menuTypes",treeMenuList);
        model.addAttribute("allPermissions",treeMenuList);

        return "rolelist";
    }

    //添加权限
    @RequestMapping("/saveSubmitPermission")
    public String saveSubmitPermission(SysPermission sysPermission){
        permissionService.addPermission(sysPermission);
        return "redirect:/toAddRole";
    }

    //保存角色和权限
    @RequestMapping("/saveRoleAndPermissions")
    public String saveRoleAndPermissions(String[] permissionIds,String name){
//        System.out.println(permissionIds);
        roleService.saveRoleAndPermissions(permissionIds,name);
        return "redirect:/toAddRole";

    }
    //角色列表
    @RequestMapping("/findRoles")
    public String findRoles(Model model){

        List<SysRole> allRoles = roleService.findAllRoles();
        if (allRoles != null) {
            model.addAttribute("allRoles",allRoles);
        }
        //查询所有的菜单和权限
        List<MenuTree> treeMenuList = new ArrayList<>();
        List<SysPermission> menu = menuService.findSysPermissionWhereTypeIsMenu();
        if (menu != null) {
            for (SysPermission sysPermission : menu) {
                MenuTree menuTree = new MenuTree();
                menuTree.setId(Integer.parseInt(sysPermission.getId()+""));
                menuTree.setName(sysPermission.getName());

                List<SysPermission> menuChildrens = menuService.findMenuChildrensByParentIDLikePermission(sysPermission.getId());
                menuTree.setChildren(menuChildrens);
                treeMenuList.add(menuTree);
            }
        }
        model.addAttribute("allMenuAndPermissions",treeMenuList);

        return "permissionlist";
    }

    //查看当前角色的权限
    @RequestMapping("/loadMyPermissions")
    @ResponseBody
    public List<SysPermission> loadMyPermissions(String roleId){
        List<SysPermission> sysPermissionList = permissionService.loadPermissionsByRoleId(roleId);
        return sysPermissionList;

    }

    //编辑当前角色的权限
    @RequestMapping("/updateRoleAndPermission")
    public String updateRoleAndPermission(String roleId,String[] permissionIds){
        permissionService.updateRolePermission(roleId,permissionIds);

        return "redirect:/findRoles";
    }

    //删除用户
    @RequestMapping("/delRole")
    public String delRole(String roleId){
        roleService.delRole(roleId);
        return "redirect:/findRoles";

    }
}
