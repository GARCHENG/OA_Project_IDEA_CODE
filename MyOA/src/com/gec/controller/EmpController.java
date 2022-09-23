package com.gec.controller;

import com.gec.domain.ActiveUser;
import com.gec.domain.Baoxiaobill;
import com.gec.domain.Employee;
import com.gec.domain.SysRole;
import com.gec.service.BaoxiaoService;
import com.gec.service.EmpService;
import com.gec.service.PermissionService;
import com.gec.service.RoleService;
import org.apache.shiro.SecurityUtils;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

@Controller
public class EmpController {
    @Autowired
    private EmpService empService;
    @Autowired
    private BaoxiaoService baoxiaoService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    //查询我的报销账单
    @RequestMapping("/myBaoxiaoBill")
    public String myBaoxiaoBill(Model model,String pageNumber){
        if (pageNumber==null){
            pageNumber="1";
        }
        int i = baoxiaoService.findBaoxiaoCount();
        model.addAttribute("totalCount",i);
        model.addAttribute("UserpageNumber",pageNumber);
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Baoxiaobill> baoxiaobillList = baoxiaoService.findBaoxiaoBillByUserID(activeUser.getUserid(),pageNumber);
        if (baoxiaobillList != null) {
            model.addAttribute("baoxiaoList",baoxiaobillList);
        }

        return "baoxiaobill";
    }
    //查看用户列表
    @RequestMapping("/findUserList")
    public String findUserList(Model model){
        List<Employee> employeeList = empService.findUserList();
        if (employeeList != null) {
            model.addAttribute("userList",employeeList);
        }
        List<SysRole> roleList = roleService.findAllRoles();
        if (roleList != null) {
            model.addAttribute("allRoles",roleList);
        }
        return "userlist";

    }

    //重新分配待办人
    @RequestMapping("/assignRole")
    @ResponseBody
    public String assignRole(String roleId, String userId){
        int i  =empService.updataRoleIDByUserName(roleId,userId);
        return i+"";

    }

    //寻找上司
    @RequestMapping("/findNextManager")
    @ResponseBody
    public List<Employee> findNextManager(String level){
        List<Employee> managerList = empService.findNextManager(level);
        return managerList;

    }

    //新建用户
    @RequestMapping("/saveUser")
    public String saveUser(Employee employee){
        empService.addUser(employee);
        return "redirect:/findUserList";

    }


    //查看当前员工的角色和权限列表
    @RequestMapping("/viewPermissionByUser")
    @ResponseBody
    public SysRole viewPermissionByUser(String userName){

        SysRole sysRole = permissionService.findPermissionByUsername(userName);
        return sysRole;

    }
    //检查用户名是否相同
    @RequestMapping("/checkUsername")
    @ResponseBody
    public String checkUsername(String name){
        int i = empService.checkUsername(name);
        return i+"";
    }
}
