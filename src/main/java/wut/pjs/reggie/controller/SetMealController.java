package wut.pjs.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wut.pjs.reggie.common.R;
import wut.pjs.reggie.dto.DishDto;
import wut.pjs.reggie.dto.SetmealDto;
import wut.pjs.reggie.entity.Category;
import wut.pjs.reggie.entity.Dish;
import wut.pjs.reggie.entity.Setmeal;
import wut.pjs.reggie.service.CategoryService;
import wut.pjs.reggie.service.SetMealDishService;
import wut.pjs.reggie.service.SetMealService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/18/10:47
 */
/*
* 套餐管理
* */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private SetMealDishService  setMealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);
        setMealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }


    /*
     * 套餐信息分页查询
     * */
    @GetMapping("/page")
    public R<Page> page (int page , int pageSize , String name){
        //构造一个分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件 like模糊查询
        queryWrapper.like(name!= null , Setmeal::getName,name);

        //添加排序条件 降序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行分页查询
        setMealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        //使用 stream流 基于 PageInfo中records的list集合处理 得到一个stream流对象，map就是把遍历的元素取出来，
        //通过Lambada表达式去处理item，创建一个新的dishDto对象 通过对象拷贝 拷贝遍历到的基本属性
        //再通过item得到的分类id 去查数据库 目的是查询到分类名称 给dishDto对象设置分类名称
        //最后把dishDto对象返回，最终得到一个List集合对象 将这个集合对象 赋值给 dishDtoPage.setRecords(list);
        List<SetmealDto> list = records.stream().map((item)->{
            //构造 一个 DishDto的对象
            SetmealDto setmealDto = new SetmealDto();
            //将item遍历到的属性值，拷贝到dishDto对象中
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据id来查询分类对象 目的是拿到分类名称
            Category category = categoryService.getById(categoryId);
            //判断得到的id是否为空
            if (categoryId != null) {
                String categoryName = category.getName();//根据id拿到分类名称
                setmealDto.setCategoryName(categoryName);//将拿到的分类名称 赋值到dishDto对象中
            }
            //返回dishDto对象
            return setmealDto;
        }).collect(Collectors.toList());
        //将List集合赋值给dishDtoPage的Records
        dtoPage.setRecords(list);
        //最后返回的是分类名称赋值后的dishDtoPage
        return R.success(dtoPage);
    }



    //根据Id查询套餐信息
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto=setMealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /*
     * 修改菜品以及口味
     * */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());

        //涉及两张表 菜品和口味表
        setMealService.updateWithDish(setmealDto);

        return R.success("修改菜品信息成功");
    }


    //停售起售菜品
    @PostMapping("/status/{status}")
    public R<String> sale(@PathVariable int status, String[] ids){
        //forEach 实现批量操作
        for(String id: ids){
            Setmeal setmeal = setMealService.getById(id);
            setmeal.setStatus(status);
            setMealService.updateById(setmeal);
        }
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);

        setMealService.deleteWithDish(ids);

        return R.success("套餐数据删除成功");
    }

    /*
    * 根据条件查询套餐数据*/
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        log.info("setmeal:{}", setmeal);
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotEmpty(setmeal.getName()), Setmeal::getName, setmeal.getName());
        queryWrapper.eq(null != setmeal.getCategoryId(), Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(null != setmeal.getStatus(), Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        return R.success(setMealService.list(queryWrapper));
    }



}
