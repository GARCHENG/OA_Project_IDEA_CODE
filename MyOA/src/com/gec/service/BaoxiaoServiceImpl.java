package com.gec.service;

import com.gec.domain.Baoxiaobill;
import com.gec.domain.BaoxiaobillExample;
import com.gec.mapper.BaoxiaobillMapper;
import com.gec.utils.Contains;
import com.github.pagehelper.PageHelper;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BaoxiaoServiceImpl implements BaoxiaoService {
    @Autowired
    private BaoxiaobillMapper baoxiaobillMapper;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private TaskService taskService;

    @Override
    public void saveStartBaoxiao(Baoxiaobill baoxiaobill,String username) {

        baoxiaobill.setCreatdate(new Date());
        baoxiaobill.setState(1);
        baoxiaobillMapper.insert(baoxiaobill);
//        System.out.println(baoxiaobill.getId());

        String businessKey = Contains.PROCESS_KEY+"."+baoxiaobill.getId();
        Map<String, Object> map = new HashMap<>();
        map.put("inputUser",username);
        runtimeService.startProcessInstanceByKey(Contains.PROCESS_KEY,businessKey,map);
        System.out.println("推进任务成功!");


    }

    @Override
    public Baoxiaobill findBaoxiaoBillByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        String businessKey = historicProcessInstance.getBusinessKey();

        String baoxiaoBillId = businessKey.substring(businessKey.lastIndexOf(".")+1);
        Baoxiaobill baoxiaobill = baoxiaobillMapper.selectByPrimaryKey(Integer.parseInt(baoxiaoBillId));
        return baoxiaobill;
    }

    @Override
    public List<Baoxiaobill> findBaoxiaoBillByUserID(String id,String pageNumber) {
        BaoxiaobillExample be = new BaoxiaobillExample();
        BaoxiaobillExample.Criteria criteria = be.createCriteria();
        criteria.andUserIdEqualTo(Integer.parseInt(id));

        PageHelper.startPage(Integer.parseInt(pageNumber),8);
        List<Baoxiaobill> baoxiaobillList = baoxiaobillMapper.selectByExample(be);
        return baoxiaobillList;
    }

    @Override
    public void deleteBillById(String id) {
        int i = baoxiaobillMapper.deleteByPrimaryKey(Integer.parseInt(id));
        if (i!=0){
            System.out.println("删除成功!");
        }

    }

    @Override
    public Baoxiaobill findBaoxiaoBillByBillID(String id) {
        Baoxiaobill baoxiaobill = baoxiaobillMapper.selectByPrimaryKey(Integer.parseInt(id));
        return baoxiaobill;
    }

    @Override
    public int findBaoxiaoCount() {
        int i = baoxiaobillMapper.countByExample(null);
        return i;
    }
}
