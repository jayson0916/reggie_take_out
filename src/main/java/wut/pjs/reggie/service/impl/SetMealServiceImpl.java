package wut.pjs.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wut.pjs.reggie.common.CustomException;

import wut.pjs.reggie.dto.SetmealDto;

import wut.pjs.reggie.entity.Setmeal;
import wut.pjs.reggie.entity.SetmealDish;
import wut.pjs.reggie.mapper.SetMealMapper;

import wut.pjs.reggie.service.SetMealDishService;
import wut.pjs.reggie.service.SetMealService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/10/10:23
 */
@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;


    //新增套餐，同时保存套餐和菜品的关联关系
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，操作setMeal 执行insert操作
        this.save(setmealDto);
        List<SetmealDish> setMealDishes = setmealDto.getSetmealDishes();

        setMealDishes = setMealDishes.stream().map(item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息，操作setMeal_dish ， 执行insert操作
        setMealDishService.saveBatch(setMealDishes);
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新setmeal表的基本信息
        this.updateById(setmealDto);

        //更新setmeal_dish表信息delete操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setMealDishService.remove(queryWrapper);
    }


    //删除套餐，同时需要删除套餐和菜品的关联关系
    @Override
    public void deleteWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        //如果不能删除，抛出一个业务异常
        if (count >0) {
            throw new CustomException("套餐正在售卖中无法删除");
        }
        //如果可以删除，先删除套餐表里的数据---setmeal
        this.removeByIds(ids);

        //在删除关系表中的数据---setmeal_dish
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setMealDishService.remove(setmealDishLambdaQueryWrapper);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //查询套餐基本信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        //查询套餐菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setMealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }


}

