package com.teamwork.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public R<Employee> login(HttpServletRequest req, @RequestBody Employee employee) {
        String pwd = employee.getPassword();
        pwd = DigestUtils.md5DigestAsHex(pwd.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // Employee::getUsername 实例化y一个Employee对象 调用Employee对象的getUsername方法
        // eq 为 = 即将 Username 的参数值为 employee.getUsername() [使用的是get方法而不是set方法]
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
        req.getSession().setAttribute("employee", re.getId());
        return R.success(re);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest req) {
        req.getSession().removeAttribute("employee");
        return R.success("退出成功...");
    }
    @GetMapping("/page")
    public R<Page<Employee>> page(Integer page, Integer pageSize, String name) {
        Page<Employee> employeePage = new Page<>(page, pageSize);
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.like(name!=null,"name",name)
                .or()
                .like(name!=null,"username",name)
                .or()
                .like(name!=null,"phone",name);
        wrapper.orderByDesc("update_time");
        employeeService.page(employeePage, wrapper);
        return R.success(employeePage);
    }
    @PutMapping
    public R<String> updateEmployee(@RequestBody Employee employee) {
        return employeeService.updateById(employee) ?
                R.success("编辑员工信息/状态成功!") : R.error("编辑员工信息/状态失败!");
    }
    @PostMapping
    public R<String> save(@RequestBody Employee employee) {
        String username = employee.getUsername();
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        // eq 等于  条件:employee.getUsername() != null
        //         列:username  查找值:username = employee.getUsername()
        wrapper.eq(username!=null,"username",username);
        if (employeeService.getOne(wrapper) != null) {
            return R.error("添加用户失败, 用户名: " + username + "已存在!");
        }
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setStatus(1);
        return employeeService.save(employee) ?
                R.success("添加用户成功!") : R.error("添加用户失败,请稍后再试!");
    }
    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }

}
