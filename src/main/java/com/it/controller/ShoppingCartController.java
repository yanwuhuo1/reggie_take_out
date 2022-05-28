package com.it.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.common.BaseContext;
import com.it.common.R;
import com.it.entity.ShoppingCart;
import com.it.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2022-05-15
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    //添加购物车
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingCart,{}", shoppingCart);
        //设置用户id，指定当前是那个用户的购物车
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            //添加到购物车的是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询当前菜品或者套餐是否在购物车中
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        if (one != null) {
            //如果已经存在，就在原来的数量基础上加一
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);

        } else {
            //如果不存在，则添加到购物车，数量默认是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;

        }
        return R.success(one);
    }

    //减少购物车
    @PostMapping("/sub")
    public R<ShoppingCart> Sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingCart,{}", shoppingCart);
        //设置用户id，指定当前是那个用户的购物车
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            //添加到购物车的是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询当前菜品或者套餐是否在购物车中
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        if (one != null) {
            //如果已经存在，就在原来的数量基础上加一
            Integer number = one.getNumber();
            one.setNumber(number - 1);

            if (one.getNumber().equals(0)) {
                shoppingCartService.removeById(one);
            }
            shoppingCartService.updateById(one);
        } else {
            //如果不存在，则添加到购物车，数量默认是一
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;

        }
        return R.success(one);
    }

    //查看购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    //清空购物车
    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("清空购物车成功");


    }

}

