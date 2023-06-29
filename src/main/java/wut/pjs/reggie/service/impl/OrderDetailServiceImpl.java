package wut.pjs.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import wut.pjs.reggie.entity.OrderDetail;
import wut.pjs.reggie.mapper.OrderDetailMapper;
import wut.pjs.reggie.service.OrderDetailService;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/23/17:50
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
