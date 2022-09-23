package com.gec.service;

import com.gec.domain.Baoxiaobill;

import java.util.List;

public interface BaoxiaoService {
    void saveStartBaoxiao(Baoxiaobill baoxiaobill,String username);

    Baoxiaobill findBaoxiaoBillByTaskId(String taskId);

    List<Baoxiaobill> findBaoxiaoBillByUserID(String id,String pageNumber);

    void deleteBillById(String id);

    Baoxiaobill findBaoxiaoBillByBillID(String id);

    int findBaoxiaoCount();
}
