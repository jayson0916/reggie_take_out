package wut.pjs.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wut.pjs.reggie.common.R;
import wut.pjs.reggie.entity.Category;

import wut.pjs.reggie.service.CategoryService;

import java.util.List;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/09/11:06
 */
/*
* 分类管理*/
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /*
    * 新增分类
    * */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("Category={}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }


    /*
     * GetMapping=page
     * 菜品信息分页查询
     * */
    @GetMapping("/page")
    public R<Page> page(int page , int pageSize){
        log.info("page= {}","pageSIze = {}","name = {}",page,pageSize);
        //构造分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //排序条件 使用sort进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询,MybatisPlus已经把数据处理好做好了返回值在Page中
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    /*
    * 根据id删除分类
    * */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类,id:{}",ids);

        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    /*
     * 根据id修改分类信息
     * */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息:{}",category);
        categoryService.updateById(category);
        return R.success("分类信息修改成功");
    }


    /*
    * 根据条件来查询分类数据
    * */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!= null,Category::getType,category.getType());
        //添加排序条件  优先使用sort排序 在使用更新时间作为第二个排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
