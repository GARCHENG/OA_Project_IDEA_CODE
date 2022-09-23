package com.gec.listener;

import com.gec.domain.ActiveUser;
import com.gec.domain.Employee;
import com.gec.service.EmpService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();

        WebApplicationContext applicationContext
                = ContextLoader.getCurrentWebApplicationContext();
        EmpService empService = (EmpService) applicationContext.getBean("empService");

        Employee employee = empService.findEmpByManagerID(activeUser.getManagerId());
        delegateTask.setAssignee(employee.getName());

    }
}
