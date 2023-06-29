package wut.pjs.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import wut.pjs.reggie.dto.DishDto;
import wut.pjs.reggie.entity.Dish;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/10/10:20
 */
@Service
public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表dish ， dishFlavor
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新对应的口味信息
    void updateWithFlavor(DishDto dishDto);
}
