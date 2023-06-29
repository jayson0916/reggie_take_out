package wut.pjs.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import wut.pjs.reggie.entity.Employee;
import wut.pjs.reggie.mapper.EmployeeMapper;
import wut.pjs.reggie.service.EmployeeService;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/04/0:50
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
