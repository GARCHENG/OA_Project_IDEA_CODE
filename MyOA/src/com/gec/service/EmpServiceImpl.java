package com.gec.service;

import com.gec.domain.Employee;
import com.gec.domain.EmployeeExample;
import com.gec.mapper.EmployeeMapper;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("empService")
public class EmpServiceImpl implements EmpService{
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Employee findEmpByName(String name) {
        EmployeeExample ue = new EmployeeExample();
        EmployeeExample.Criteria criteria = ue.createCriteria();
        criteria.andNameEqualTo(name);
        List<Employee> list = employeeMapper.selectByExample(ue);
        if (list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public Employee findEmpByManagerID(Long managerId) {
        Employee employee = employeeMapper.selectByPrimaryKey(managerId);
        return employee;
    }

    @Override
    public List<Employee> findUserList() {
        List<Employee> employeeList = employeeMapper.selectUserListWithRoleNameWithManagerName();
        return employeeList;
    }

    @Override
    public int updataRoleIDByUserName(String roleId, String userId) {
        Employee emp  = new Employee();
        emp.setRole(Integer.parseInt(roleId));
        EmployeeExample ee = new EmployeeExample();
        EmployeeExample.Criteria criteria = ee.createCriteria();
        criteria.andNameEqualTo(userId);
        int i = employeeMapper.updateByExampleSelective(emp, ee);
        return i;
    }

    @Override
    public List<Employee> findNextManager(String level) {
        EmployeeExample ue  =new EmployeeExample();
        EmployeeExample.Criteria criteria = ue.createCriteria();
        criteria.andRoleEqualTo(Integer.parseInt(level)+1);
        List<Employee> employeeList = employeeMapper.selectByExample(ue);
        return employeeList;
    }

    @Override
    public void addUser(Employee employee) {
        //处理用户输进来的密码
        employee.setSalt("eteokues");
        Md5Hash md5Hash = new Md5Hash(employee.getPassword(),employee.getSalt(),2);
        employee.setPassword(md5Hash.toString());
        employeeMapper.insertSelective(employee);
    }

    @Override
    public int checkUsername(String name) {
        EmployeeExample ue = new EmployeeExample();
        EmployeeExample.Criteria criteria = ue.createCriteria();
        criteria.andNameEqualTo(name);
        List<Employee> employeeList = employeeMapper.selectByExample(ue);
        if (employeeList!=null&&employeeList.size()>0){
            return 1;
        }
        return 0;
    }
}
