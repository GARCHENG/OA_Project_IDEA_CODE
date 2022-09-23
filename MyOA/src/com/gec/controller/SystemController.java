package com.gec.controller;

import com.gec.domain.ActiveUser;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SystemController {
    @RequestMapping("/login")
    public String login(HttpServletRequest request){
        String shiroLoginFailure = (String) request.getAttribute("shiroLoginFailure");
        if (shiroLoginFailure != null) {
            if (shiroLoginFailure.equals(UnknownAccountException.class.getName())){
                request.setAttribute("errorMsg","没有该用户!");
            }
            if (shiroLoginFailure.equals(IncorrectCredentialsException.class.getName())){
                request.setAttribute("errorMsg","密码错误!");
            }
        }
        return "login";
    }
    @RequestMapping("/home")
    public String home(Model model){
        Subject subject = SecurityUtils.getSubject();
        ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
        model.addAttribute("activeUser",activeUser);
        return "index";

    }
}
