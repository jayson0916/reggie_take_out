package wut.pjs.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import wut.pjs.reggie.entity.Orders;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/23/17:46
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
