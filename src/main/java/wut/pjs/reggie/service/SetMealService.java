package wut.pjs.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import wut.pjs.reggie.dto.SetmealDto;
import wut.pjs.reggie.entity.Setmeal;

import java.util.List;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/10/10:21
 */
@Service
public interface SetMealService extends IService<Setmeal> {
    //新增套餐，同时保存套餐和菜品的关联关系
    void saveWithDish(SetmealDto setmealDto);

    void updateWithDish(SetmealDto setmealDto);

    //删除套餐，同时需要删除套餐和菜品的关联关系
    void deleteWithDish(List<Long> ids);

    //根据id查询套餐信息
    SetmealDto getByIdWithDish(Long id);
}
