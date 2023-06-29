package wut.pjs.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wut.pjs.reggie.common.CustomException;
import wut.pjs.reggie.entity.Category;
import wut.pjs.reggie.entity.Dish;
import wut.pjs.reggie.entity.Setmeal;
import wut.pjs.reggie.mapper.CategoryMapper;
import wut.pjs.reggie.service.CategoryService;
import wut.pjs.reggie.service.DishService;
import wut.pjs.reggie.service.SetMealService;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/09/11:10
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;

    //根据id删除分类，删除之前进行判断
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品，如果存在关联，抛出一个业务异常
        if (count1 >0) {
            //count1大于0 说明关联了菜品 抛出业务异常
            throw new CustomException("当前分类关联了菜品，不能删除！");
        }

        LambdaQueryWrapper<Setmeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setMealService.count(setMealLambdaQueryWrapper);
        //查询当前分类是否关联了套餐，如果存在关联，抛出一个业务异常
        if (count2 >0) {
            //count2大于0 说明关联了套餐 抛出业务异常
            throw new CustomException("当前分类关联了套餐，不能删除！");
        }
        //都没有关联，正常删除分类
        super.removeById(id);
    }
}
