package wut.pjs.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import wut.pjs.reggie.entity.Orders;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/23/17:49
 */
@Service
public interface OrdersService extends IService<Orders> {


    void submit(Orders orders);
}
