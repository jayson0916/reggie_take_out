package wut.pjs.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wut.pjs.reggie.common.BaseContext;
import wut.pjs.reggie.common.R;
import wut.pjs.reggie.entity.ShoppingCart;
import wut.pjs.reggie.service.ShoppingCartService;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @Author: Jayson_P
 * @Date: 2023/06/22/9:56
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController  {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /*
     * 清空购物车
     * */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return R.success("清空成功");
    }

    /*
    * 购物车新增
    * */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //查看来自前端的json数据
        log.info(shoppingCart.toString());

        //设置用户id ， 指定当前是那个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        Long dishId = shoppingCart.getDishId();
        LocalDateTime now = LocalDateTime.now();
        shoppingCart.setCreateTime(now);
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

//        if (shoppingCart.getDishFlavor()==null){
//            R.error("口味不能为空");
//        }
        if (dishId != null) {
            //添加到购物车的是菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        }else {
            //添加到购物车的是套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }


        //查询菜品或套餐 是否存在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        //如果已经存在，就在原来数量基础上数量加1
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //如果不存在，则添加到购物车 数量默认为1
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }


    /*
    * 查看购物车
    * */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(list);
    }


    /*
    * 在购物车里减去一件商品
    * */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){

        log.info(shoppingCart.toString());
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (shoppingCart.getDishId()!=null) {

            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }
        else {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        Integer Number = cartServiceOne.getNumber();
        if (Number == 1) {
            shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        } else {
             cartServiceOne.setNumber(Number-1);
            shoppingCartService.updateById(cartServiceOne);
        }
        return R.success("减去菜品成功");
    }


}
