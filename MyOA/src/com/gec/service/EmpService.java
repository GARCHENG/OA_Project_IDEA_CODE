package com.gec.service;

import com.gec.domain.Employee;

import java.util.List;

public interface EmpService {
    Employee findEmpByName(String name);

    Employee findEmpByManagerID(Long managerId);

    List<Employee> findUserList();

    int updataRoleIDByUserName(String roleId, String username);

    List<Employee> findNextManager(String level);

    void addUser(Employee employee);

    int checkUsername(String name);
}
