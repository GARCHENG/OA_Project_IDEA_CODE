package com.gec.service;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface WorkFlowService {
    void deployProcess(String name, InputStream inputStream);

    List<Task> findTaskByAssignee(String username);

    List<String> findOutcomeByTaskID(String taskId);

    List<Comment> findCommentsByTaskID(String taskId);

    void completeTaskByTaskID(String taskId, String outcome ,String comment,String username);

    List<Comment> findHisCommentByBillID(String id);

    List<Deployment> findDeploymentList();

    List<ProcessDefinition> findProcessInstanceList();

    void delDeployment(String deploymentId);

    InputStream findPngByDepID(String deploymentId, String imageName);

    Map<String, Object> findLocationByBill(String id);

    Map<String, Object> findLocationByTaskID(String taskId);
}
