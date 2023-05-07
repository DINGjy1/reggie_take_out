package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Request;
import org.example.reggie.common.R;
import org.example.reggie.entity.Employee;
import org.example.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
/*
员工登录
 */

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee) {

        /*处理逻辑：
        1.将页面提交密码password进行md5加密处理
        2.将username查询数据库
        3.没有查询到返回登录失败结果
        4.密码比对，不一致返回登陆失败
        5.查看员工状态，是否已禁用，返回结果
        6.登录成功，将员工id存入session并返回登陆成功结果
         */

        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp =employeeService.getOne(queryWrapper);

        if(emp ==null) {
            return R.error("账号不存在，登录失败！");
        }

        if(!emp.getPassword().equals(password)){
            return R.error("密码错误，登录失败！");
        }

        if (emp.getStatus()==0){
            return R.error("账号已禁用！");
        }

        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    /*
    员工退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /*
    新增员工
     */
    @PostMapping
    public R<String>save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置初始密码123456，并进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获取当前用户id
        //Long empId=(Long) request.getSession().getAttribute("employee");

        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功！");
    }
    /*
    员工分页查询
     */
    @GetMapping("page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //分页构造器
        Page pageInfo =new Page(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper =new LambdaQueryWrapper();

        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getUsername,name);

        //排序条件
        queryWrapper.orderByAsc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }
    /*
    根据id修改员工信息
    */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        long id=Thread.currentThread().getId();
        log.info("线程id为：{}",id);

        //Long empId=(Long) request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功！");

    }
    /*
    根据id查询员工信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee=employeeService.getById(id);
        if (employee!=null) {
            return R.success(employee);
        }
        return R.error("没有查询到该员工");
    }
}
