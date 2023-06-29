package wut.pjs.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wut.pjs.reggie.common.BaseContext;
import wut.pjs.reggie.common.CustomException;
import wut.pjs.reggie.entity.*;
import wut.pjs.reggie.mapper.OrdersMapper;
import wut.pjs.reggie.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/23/17:49
 */

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    //购物车service
    @Autowired
    private ShoppingCartService shoppingCartService;
    //用户Service
    @Autowired
    private UserService userService;
    //用户地址Service
    @Autowired
    private AddressBookService addressBookService;
    //订单细节表
    @Autowired
    private OrderDetailService orderDetailService;

    /*
    *用户下单*/
    @Override
    @Transactional//操作多次数据库 加入事务注解
    public void submit(Orders orders) {
        //获取当前用户ID
        Long currentId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        //如果购物车为空 抛业务异常
        if (shoppingCartList.size() == 0) {
            throw new CustomException("当前用户购物车为空，不能下单");
        }
        //查询用户数据
        User userInfo = userService.getById(currentId);

        //查询用户地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBookInfo = addressBookService.getById(addressBookId);
        if (addressBookInfo == null) {
            throw new CustomException("当前用户地址信息有无，不能下单");
        }

        //生成orderId订单号
        long orderId = IdWorker.getId();
        //使用原子整数类 保证多线程下的线程安全 保证计算的正确性    //原子类通过CAS机制保证线程安全
        AtomicInteger amount = new AtomicInteger(0);


        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());//BigDecimal类不能直接使用数字计算
            return orderDetail;
        }).collect(Collectors.toList());

        //向订单表插入一条数据

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(userInfo.getName());
        orders.setConsignee(addressBookInfo.getConsignee());
        orders.setPhone(addressBookInfo.getPhone());
        orders.setAddress((addressBookInfo.getProvinceName() == null ? "" : addressBookInfo.getProvinceName())
                + (addressBookInfo.getCityName() == null ? "" : addressBookInfo.getCityName())
                + (addressBookInfo.getDistrictName() == null ? "" : addressBookInfo.getDistrictName())
                + (addressBookInfo.getDetail() == null ? "" : addressBookInfo.getDetail()));

        this.save(orders);
        //订单明细表插入多条数据。 不止一条 不同的菜品套餐
        orderDetailService.saveBatch(orderDetails);
        //下单完成后，清除购物车数据
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }
}
