package com.gec.controller;

import com.gec.domain.ActiveUser;
import com.gec.domain.Baoxiaobill;
import com.gec.service.BaoxiaoService;
import com.gec.service.WorkFlowService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.omg.CORBA.StringHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Controller
public class WorkFlowController {
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private BaoxiaoService baoxiaoService;


    //发布流程
    @RequestMapping("/deployProcess")
    public String deployProcess(String processName, MultipartFile file){
        try {
            InputStream inputStream = file.getInputStream();
            workFlowService.deployProcess(processName,inputStream);
            return "redirect:/welcome.html";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //启动流程
    @RequestMapping("/saveStartBaoxiao")
    public String saveStartBaoxiao(Baoxiaobill baoxiaobill){
        Subject subject = SecurityUtils.getSubject();
        ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
        baoxiaobill.setUserId(Integer.parseInt(activeUser.getUserid()));
        baoxiaoService.saveStartBaoxiao(baoxiaobill,activeUser.getUsername());
        return "redirect:/myTaskList";

    }

    //查看当前用户的所有任务
    @RequestMapping("/myTaskList")
    public String myTaskList(Model model){
        Subject subject = SecurityUtils.getSubject();
        ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
        List<Task> taskList = workFlowService.findTaskByAssignee(activeUser.getUsername());
        if (taskList != null) {
            model.addAttribute("taskList",taskList);
        }
        return "workflow_task";

    }

    //回显报销信息
    @RequestMapping("/viewTaskForm")
    public String viewTaskForm(String taskId,Model model){
        //根据taskID查询报销账单
        Baoxiaobill baoxiaobill = baoxiaoService.findBaoxiaoBillByTaskId(taskId);
        if (baoxiaobill!=null){
            model.addAttribute("baoxiaoBill",baoxiaobill);
            model.addAttribute("taskId",taskId);
        }
        //根据taskID查询当前代办人的处理按钮
        List<String> outcomeList = workFlowService.findOutcomeByTaskID(taskId);
        if (outcomeList != null) {
            model.addAttribute("outcomeList",outcomeList);
        }
        //查询出历史批注
        List<Comment> commentList = workFlowService.findCommentsByTaskID(taskId);
        if (commentList != null) {
            model.addAttribute("commentList",commentList);
        }

        return "approve_baoxiao";


    }

    //推进任务
    @RequestMapping("/submitTask")
    public String submitTask(String taskId,String outcome,String comment){
        Subject subject = SecurityUtils.getSubject();
        ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
        workFlowService.completeTaskByTaskID(taskId,outcome,comment,activeUser.getUsername());
        return "redirect:/myTaskList";

    }

    //查看历史的comment
    @RequestMapping("/viewHisComment")
    public String viewHisComment(String id,Model model){
        //回显报销账单的信息 通过账单ID
        Baoxiaobill baoxiaobill = baoxiaoService.findBaoxiaoBillByBillID(id);
        if (baoxiaobill != null) {
            model.addAttribute("baoxiaoBill",baoxiaobill);
        }
        //根据账单id查询历史批注信息
        List<Comment> commentList = workFlowService.findHisCommentByBillID(id);
        if (commentList != null) {
            model.addAttribute("commentList",commentList);
        }

        return "workflow_commentlist";
    }

    //查看流程
    @RequestMapping("/processDefinitionList")
    public String processDefinitionList(Model model){
        //查看部署定义列表
        List<Deployment> deploymentList = workFlowService.findDeploymentList();
        if (deploymentList != null) {
            model.addAttribute("depList",deploymentList);
        }

        //查看流程定义列表
        List<ProcessDefinition> processInstanceList = workFlowService.findProcessInstanceList();
        if (processInstanceList != null) {
            model.addAttribute("pdList",processInstanceList);
        }
        return "workflow_list";
    }

    //删除流程部署
    @RequestMapping("/delDeployment")
    public String delDeployment(String deploymentId){
        workFlowService.delDeployment(deploymentId);
        return "redirect:/processDefinitionList";
    }

    //查看流程定义图
    @RequestMapping("/viewImage")
    public String viewImage(String deploymentId, String imageName, HttpServletResponse response){
        try {
            OutputStream outputStream = response.getOutputStream();
            InputStream inputStream = workFlowService.findPngByDepID(deploymentId,imageName);
            byte[] data = new byte[1024];
            int len;
            while ((len=inputStream.read(data))!=-1){
                outputStream.write(data,0,len);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;

    }

    @RequestMapping("/viewCurrentImage")
    public  String viewCurrentImage(Model model,String taskId){
        Map<String, Object> map = workFlowService.findLocationByTaskID(taskId);
        if (map != null) {
            model.addAttribute("acs", map);
            model.addAttribute("deploymentId",map.get("deploymentId"));
            model.addAttribute("imageName",map.get("imageName"));
        }
        return "viewimage";

    }

}
