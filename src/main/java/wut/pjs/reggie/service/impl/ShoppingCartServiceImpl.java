package wut.pjs.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import wut.pjs.reggie.entity.ShoppingCart;
import wut.pjs.reggie.mapper.ShoppingCartMapper;
import wut.pjs.reggie.service.ShoppingCartService;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/22/16:15
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper,ShoppingCart> implements ShoppingCartService {
}
