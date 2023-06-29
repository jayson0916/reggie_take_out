package wut.pjs.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import wut.pjs.reggie.entity.Dish;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/10/10:17
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
