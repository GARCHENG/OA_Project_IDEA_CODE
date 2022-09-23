package com.gec.controller;

import com.gec.service.BaoxiaoService;
import com.gec.service.WorkFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class BaoxiaoController {
    @Autowired
    private BaoxiaoService baoxiaoService;
    @Autowired
    private WorkFlowService workFlowService;

    @RequestMapping("/leaveBillAction_delete")
    public String leaveBillAction_delete(String id){
        baoxiaoService.deleteBillById(id);
        return "redirect:/myBaoxiaoBill";

    }

    //查看当前的流程图
    @RequestMapping("/viewCurrentImageByBill")
    public String viewCurrentImageByBill(String billId, Model model) {
        Map<String, Object> map = workFlowService.findLocationByBill(billId);
        if (map != null) {
            model.addAttribute("acs", map);
            model.addAttribute("deploymentId",map.get("deploymentId"));
            model.addAttribute("imageName",map.get("imageName"));
        }
        return "viewimage";
    }
}
