package wut.pjs.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wut.pjs.reggie.dto.DishDto;
import wut.pjs.reggie.entity.Dish;
import wut.pjs.reggie.entity.DishFlavor;
import wut.pjs.reggie.mapper.DishMapper;
import wut.pjs.reggie.service.DishFlavorService;
import wut.pjs.reggie.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/10/10:21
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /*
    * 新增菜品，同时保存对应的口味
    * */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish表
        this.save(dishDto);

        //菜品ID
        Long disId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //处理集合
        flavors = flavors.stream().map((item) ->{
            item.setDishId(disId);
            return item;
        }).collect(Collectors.toList());

        //保存口味数据到菜品口味表dishFlavor
        dishFlavorService.saveBatch(flavors);
    }



    /*
    * 根据id查询菜品信息和对应的口味信息
    * */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        //拷贝对象 拷贝菜品基本属性
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品的口味信息 从dishFlavor查
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        //向dishDto内的口味集合进行赋值
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /*
    * 更新菜品信息，同时更新对应的口味信息
    * */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表的基本信息
        this.updateById(dishDto);

        //先清理当前菜品对应的口味数据---dish flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        //处理集合
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
