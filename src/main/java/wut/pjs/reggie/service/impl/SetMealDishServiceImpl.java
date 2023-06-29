package wut.pjs.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wut.pjs.reggie.entity.SetmealDish;
import wut.pjs.reggie.mapper.SetMealDishMapper;
import wut.pjs.reggie.service.SetMealDishService;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/18/10:46
 */
@Slf4j
@Service
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetmealDish> implements SetMealDishService {
}
