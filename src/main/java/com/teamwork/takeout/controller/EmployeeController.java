package com.teamwork.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teamwork.takeout.common.R;
import com.teamwork.takeout.entity.Employee;
import com.teamwork.takeout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String pwd = employee.getPassword();
        pwd = DigestUtils.md5DigestAsHex(pwd.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee re = employeeService.getOne(queryWrapper);
        if (re == null) {
            return R.error("登陆失败...");
        }
        if (!re.getPassword().equals(pwd)) {
            return R.error("密码或用户名错误...");
        }
        if (re.getStatus() == 0) {
            return R.error("账号已被封禁...");
        }
        request.getSession().setAttribute("employee", re.getId());
        return R.success(re);
    }

}
