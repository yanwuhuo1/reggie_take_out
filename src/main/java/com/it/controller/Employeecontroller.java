package com.it.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.common.R;
import com.it.entity.Employee;
import com.it.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.*;
import sun.util.locale.provider.LocaleResources;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

@Slf4j
@RestController
@RequestMapping("/employee")
public class Employeecontroller {
    @Autowired
    private EmployeeService employeeService;

    //员工登录
    @PostMapping("/login")
    private R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee one = employeeService.getOne(queryWrapper);
        //如果没有查询到则返回登入失败结果
        if (one == null) {
            return R.error("没有此账号登入失败");
        }
        //比对密码
        if (!one.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        //查看员工状态，如果为禁用状态，则返回员工以禁用结果
        if (one.getStatus() == 0) {
            return R.error("账户以禁用");
        }
        // 登入成功，将员工id存入到session并返回登入结果
        request.getSession().setAttribute("employee", one.getId());
        return R.success(one);
    }

    //员工退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理session中保存的当前登录员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("1234567".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //获取当前登入用户的id
//        Long employeeid = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(employeeid);
//        employee.setUpdateUser(employeeid);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }
    //员工信息分页查询
    @GetMapping("/page")
    public R<Page>page(int page,int pageSize,String name){
        //构造分页构造器
        Page page1 = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(page1,lambdaQueryWrapper);

        return R.success(page1);
    }

    //根据id修改员工的值
    @PutMapping
    public R<String>update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
//        Long employee1 = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(employee1);
        employeeService.updateById(employee);
        return R.success("员工消息修改成功");
    }
    //回显数据
    @GetMapping("{id}")
    public R<Employee> getById(@PathVariable long id){
        Employee byId = employeeService.getById(id);
        if (byId!=null){
            return R.success(byId);
        }
        return R.error("没查询到员工信息");

    }

}
