package wut.pjs.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import wut.pjs.reggie.common.R;
import wut.pjs.reggie.entity.Employee;
import wut.pjs.reggie.service.EmployeeService;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/04/0:51
 */
/*
* 员工用户管理
* */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /*
    * PostMapping：login
    * 员工登录
    * */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        //1.将页面提交的密码password使用MD5加密处理
        String password = employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到则返回登录结果为error登录失败结果
        if (emp==null){
            return R.error("登陆失败");
        }

        //4.密码比对，如果不一致返回error登陆失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }

        //5.查看用户状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus()==0){
            return R.error("账号已禁用");
        }

        //6.登录成功，将用户ID放入session中并返回登录成功的结果success
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /*
     * PostMapping：logout
     * 员工退出登录
     * */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //1.清理Session中保存的当前登录员工的ID
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /*
    *PostMapping：save
    *新增员工信息
    *  */
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
        log.info("新增员工，员工信息：{}",employee.toString());
        //设置初始密码123456,并使用MD5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置创建时间和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //创建和修改操作的用户ID
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        //调用service使用MybatisPlus的IService里的save方法保存数据
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /*
    * GetMapping=page
    * 员工信息分页查询
    * */
    @GetMapping("/page")
    public R<Page> page(int page , int pageSize , String name){
        log.info("page = {}","pageSIze = {}","name = {}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询,MybatisPlus已经把数据处理好做好了返回值在Page中
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /*
    * PutMapping=update
    * 修改员工信息
    * */
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request) {
        //打印日志前端传来的信息
        log.info(employee.toString());
        //修改操作的用户ID和修改时间
        //优化后，使用mybatisPlus公共填充
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);

        employeeService.updateById(employee);
        return R.success("员工信息修改成功！");
    }


    /*
    * 根据id查询员工信息
    * */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到对应的员工信息");
    }
}
