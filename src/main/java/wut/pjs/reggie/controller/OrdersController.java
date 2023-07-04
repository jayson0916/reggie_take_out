package wut.pjs.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wut.pjs.reggie.common.R;
import wut.pjs.reggie.entity.Orders;
import wut.pjs.reggie.service.OrdersService;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/23/17:46
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /*
    * 用户下单 接口
    * */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("用户下单的订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("用户下单成功");
    }

    /*
    * 用户历史订单页面查询
    * */
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        //构造分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        //排序条件 使用下单时间进行排序
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询,MybatisPlus已经把数据处理好做好了返回值在Page中
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //构造分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        //排序条件 使用下单时间进行排序
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询,MybatisPlus已经把数据处理好做好了返回值在Page中
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

}
