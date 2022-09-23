package com.gec.service;

import com.gec.domain.Baoxiaobill;
import com.gec.mapper.BaoxiaobillMapper;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Service
public class WorkFlowServiceImpl implements WorkFlowService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private BaoxiaobillMapper baoxiaobillMapper;


    @Override
    public void deployProcess(String name, InputStream inputStream) {
        ZipInputStream zip = new ZipInputStream(inputStream);
        repositoryService.createDeployment()
                .name(name)
                .addZipInputStream(zip)
                .deploy();
        System.out.println("部署成功!");
    }

    @Override
    public List<Task> findTaskByAssignee(String username) {
        List<Task> list = taskService.createTaskQuery().taskAssignee(username).orderByDueDate().desc().list();
        return list;
    }

    @Override
    public List<String> findOutcomeByTaskID(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

        ProcessDefinitionEntity processDefinitionEntity
                = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        ActivityImpl activity = processDefinitionEntity.findActivity(processInstance.getActivityId());
        List<PvmTransition> outgoingTransitions = activity.getOutgoingTransitions();
        List<String> outcomeList = new ArrayList<>();
        if (outgoingTransitions != null && outgoingTransitions.size() > 0) {
            for (PvmTransition outgoingTransition : outgoingTransitions) {
                String name = (String) outgoingTransition.getProperty("name");
                if (name == null) {
                    outcomeList.add("默认提交");
                    return outcomeList;
                }
                outcomeList.add(name);
            }
        }
        return outcomeList;
    }

    @Override
    public List<Comment> findCommentsByTaskID(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        List<Comment> commentList = taskService.getProcessInstanceComments(task.getProcessInstanceId());
        return commentList;
    }

    @Override
    public void completeTaskByTaskID(String taskId, String outcome, String comment, String username) {
        //选择走哪条路
        Map<String, Object> map = new HashMap<>();
        map.put("message", outcome);
        //添加备注
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Authentication.setAuthenticatedUserId(username);
        taskService.addComment(taskId, task.getProcessInstanceId(), comment);

        taskService.complete(taskId, map);

        //判断该流程是否已经全部结束
        Task result = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        if (result == null) {
            //从businesskey中获得bill 的id
            Baoxiaobill baoxiaobill = new Baoxiaobill();
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            String businessKey = historicProcessInstance.getBusinessKey();
            String baoxiaoBillId = businessKey.substring(businessKey.lastIndexOf(".") + 1);
            //去数据库修改bill的状态
            baoxiaobill.setId(Integer.parseInt(baoxiaoBillId + ""));
            baoxiaobill.setState(2);
            int i = baoxiaobillMapper.updateByPrimaryKeySelective(baoxiaobill);
            if (i != 0) {
                System.out.println("修改成功!");
            }

        }
    }

    @Override
    public List<Comment> findHisCommentByBillID(String id) {
        //通过billid找到流程实例ID businessKey
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
        if (list != null) {
            for (HistoricProcessInstance historicProcessInstance : list) {
                String businessKey = historicProcessInstance.getBusinessKey();
                //截取businesskey去匹配id
                if (businessKey.substring(businessKey.lastIndexOf(".") + 1).equals(id)) {
                    //得到匹配的流程部署ID
                    String processInstanceId = historicProcessInstance.getId();

                    //通过流程定义ID得到comments
                    List<Comment> processInstanceComments = taskService.getProcessInstanceComments(processInstanceId);
                    return processInstanceComments;
                }
            }
        }
        return null;
    }

    @Override
    public List<Deployment> findDeploymentList() {
        List<Deployment> list = repositoryService.createDeploymentQuery().orderByDeploymentId().desc().list();

        return list;
    }

    @Override
    public List<ProcessDefinition> findProcessInstanceList() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().desc().list();

        return list;
    }

    @Override
    public void delDeployment(String deploymentId) {
        //强制删除流程部署
        repositoryService.deleteDeployment(deploymentId, true);
    }

    @Override
    public InputStream findPngByDepID(String deploymentId, String imageName) {
        InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, imageName);

        return resourceAsStream;
    }

    @Override
    public Map<String, Object> findLocationByBill(String id) {
        Map<String, Object> map = new HashMap<>();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
        if (list != null) {
            for (HistoricProcessInstance historicProcessInstance : list) {
                String businessKey = historicProcessInstance.getBusinessKey();
                //截取businesskey去匹配id
                if (businessKey.substring(businessKey.lastIndexOf(".") + 1).equals(id)) {
                    //得到匹配的流程部署ID
                    String processDefinitionId = historicProcessInstance.getProcessDefinitionId();

                    ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

                    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
                    map.put("deploymentId",processDefinition.getDeploymentId());
                    map.put("imageName",processDefinition.getDiagramResourceName());

                    String activityId = runtimeService.createProcessInstanceQuery().processInstanceId(historicProcessInstance.getId()).singleResult().getActivityId();
                    ActivityImpl activity = processDefinitionEntity.findActivity(activityId);
                    if (activity != null) {
                        map.put("x",activity.getX());
                        map.put("y",activity.getY());
                        map.put("height",activity.getHeight());
                        map.put("width",activity.getWidth());
                    }

                }

            }
        }
        return map;
    }

    @Override
    public Map<String, Object> findLocationByTaskID(String taskId) {
        Map<String,Object> map = new HashMap<>();

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        String activityId = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult().getActivityId();

        map.put("deploymentId",processDefinitionEntity.getDeploymentId());
        map.put("imageName",processDefinitionEntity.getDiagramResourceName());

        ActivityImpl activity = processDefinitionEntity.findActivity(activityId);
        if (activity != null) {
            map.put("x",activity.getX());
            map.put("y",activity.getY());
            map.put("height",activity.getHeight());
            map.put("width",activity.getWidth());
        }
        return map;
    }
}