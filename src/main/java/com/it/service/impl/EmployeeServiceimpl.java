package com.it.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.entity.Employee;
import com.it.mapper.EmployeeMapper;
import com.it.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceimpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {


}
