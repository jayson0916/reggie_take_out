package wut.pjs.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import wut.pjs.reggie.common.R;
import wut.pjs.reggie.dto.DishDto;
import wut.pjs.reggie.entity.Category;
import wut.pjs.reggie.entity.Dish;
import wut.pjs.reggie.entity.DishFlavor;
import wut.pjs.reggie.service.CategoryService;
import wut.pjs.reggie.service.DishFlavorService;
import wut.pjs.reggie.service.DishService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/14/10:42
 */
/*
* 菜品管理
* */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;
    /*
    * 新增菜品以及口味
    * */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /*
    * 菜品信息分页查询
    * */
    @GetMapping("/page")
    public R<Page> page (int page , int pageSize ,String name){
        //构造一个分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);

        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        //使用 stream流 基于 PageInfo中records的list集合处理 得到一个stream流对象，map就是把遍历的元素取出来，
        //通过Lambada表达式去处理item，创建一个新的dishDto对象 通过对象拷贝 拷贝遍历到的基本属性
        //再通过item得到的分类id 去查数据库 目的是查询到分类名称 给dishDto对象设置分类名称
        //最后把dishDto对象返回，最终得到一个List集合对象 将这个集合对象 赋值给 dishDtoPage.setRecords(list);
        List<DishDto> list = records.stream().map((item)->{
            //构造 一个 DishDto的对象
            DishDto dishDto = new DishDto();
            //将item遍历到的属性值，拷贝到dishDto对象中
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);//根据id来查询分类对象 目的是拿到分类名称
            //判断得到的id是否为空
            if (categoryId != null) {
                String categoryName = category.getName();//根据id拿到分类名称
                dishDto.setCategoryName(categoryName);//将拿到的分类名称 赋值到dishDto对象中
            }
            //返回dishDto对象
            return dishDto;
        }).collect(Collectors.toList());
        //将List集合赋值给dishDtoPage的Records
        dishDtoPage.setRecords(list);
        //最后返回的是分类名称赋值后的dishDtoPage
        return R.success(dishDtoPage);
    }


    /*
    * 根据id查询菜品信息和对应的口味信息
    * */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        //传入前端的id进行查询
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }


    /*
     * 修改菜品以及口味
     * */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        //涉及两张表 菜品和口味表

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品信息成功");
    }


    //停售起售菜品
    @PostMapping("/status/{status}")
    public R<String> sale(@PathVariable int status, String[] ids){
        //forEach 实现批量操作
        for(String id: ids){
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }

    //删除菜品
    @DeleteMapping
    public R<String> delete(String[] ids){
        //forEach 实现批量操作
        for (String id:ids) {
            dishService.removeById(id);
        }
        return R.success("删除成功");
    }


    /*
    * 根据条件查询对应的菜品数据
    * */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        //添加条件 状态查询为1（起售状态）的菜品
//        queryWrapper.eq(Dish::getStatus,1);
//        //排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus(); //dish_id_status
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果存在，直接返回，无需查询数据库
        if (dishDtoList != null) {
            return R.success(dishDtoList);
        }
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件 状态查询为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item)->{
            //构造 一个 DishDto的对象
            DishDto dishDto = new DishDto();
            //将item遍历到的属性值，拷贝到dishDto对象中
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);//根据id来查询分类对象 目的是拿到分类名称
            //判断得到的id是否为空
            if (categoryId != null) {
                String categoryName = category.getName();//根据id拿到分类名称
                dishDto.setCategoryName(categoryName);//将拿到的分类名称 赋值到dishDto对象中
            }

            //当前菜品ID
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);

            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);
            //返回dishDto对象
            return dishDto;
        }).collect(Collectors.toList());
        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到redis中 设置缓存时间60min
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }
}
