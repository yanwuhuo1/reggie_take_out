package com.it.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.common.BaseContext;
import com.it.common.R;
import com.it.entity.ShoppingCart;
import com.it.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


//    //添加购物车
//    @PostMapping("/add")
//    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
//        log.info("shoppingCart,{}", shoppingCart);
//        //设置用户id，指定当前是那个用户的购物车
//
//        Long currentId = BaseContextUtil.getCurrentId();
//        shoppingCart.setUserId(currentId);
//
//        Long dishId = shoppingCart.getDishId();
//        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
//        if (dishId != null) {
//            //添加到购物车的是菜品
//            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
//        } else {
//            //添加到购物车的是套餐
//            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
//        }
//        //查询当前菜品或者套餐是否在购物车中
//        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
//        if (one != null) {
//            //如果已经存在，就在原来的数量基础上加一
//            Integer number = one.getNumber();
//            one.setNumber(number + 1);
//            shoppingCartService.updateById(one);
//
//        } else {
//            //如果不存在，则添加到购物车，数量默认是一
//            shoppingCart.setNumber(1);
//            shoppingCart.setCreateTime(LocalDateTime.now());
//            shoppingCartService.save(shoppingCart);
//            one = shoppingCart;
//
//        }
//        return R.success(one);
//    }
//
//    //减少购物车
//    @PostMapping("/sub")
//    public R<ShoppingCart> Sub(@RequestBody ShoppingCart shoppingCart) {
//        log.info("shoppingCart,{}", shoppingCart);
//        //设置用户id，指定当前是那个用户的购物车
//        Long currentId = BaseContextUtil.getCurrentId();
//        shoppingCart.setUserId(currentId);
//
//        Long dishId = shoppingCart.getDishId();
//        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
////        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
////        QueryWrapper<ShoppingCart> lambdaQueryWrapper = new QueryWrapper<>();
//        if (dishId != null) {
//            //添加到购物车的是菜品
//            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
////            lambdaQueryWrapper.eq("dishId",dishId);
//        } else {
//            //添加到购物车的是套餐
//            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
////            lambdaQueryWrapper.eq("setmealId",shoppingCart.getSetmealId());
//        }
//        //查询当前菜品或者套餐是否在购物车中
//        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
//        if (one != null) {
//            //如果已经存在，就在原来的数量基础上加一
//            Integer number = one.getNumber();
//            one.setNumber(number - 1);
//
//            if (one.getNumber().equals(0)) {
//                shoppingCartService.removeById(one);
//            }
//            shoppingCartService.updateById(one);
//        } else {
//            //如果不存在，则添加到购物车，数量默认是一
//            shoppingCart.setNumber(1);
//            shoppingCartService.save(shoppingCart);
//            one = shoppingCart;
//
//        }
//        return R.success(one);
//    }
    @PostMapping("/add")
    public R<ShoppingCart> addDishToShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingCart:{}", shoppingCart);
        //当前线程id即用户id
        Long currentId = BaseContext.getCurrentId();

        //当前添加进购物车的菜品id或者套餐id
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        shoppingCart.setUserId(currentId);
        //先通过条件构造器查询
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(currentId != null, ShoppingCart::getUserId, currentId);

        //判断此次添加进购物车的是菜品还是套餐，通过dishId和setMealId判断
        if (setmealId != null) {
            //设置查询当前套餐信息条件
            queryWrapper.eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);
        } else if (dishId != null) {
            //设置查询当前菜品信息条件
            queryWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
        }

        //判断当前菜品id或套餐id在购物车中是否对应
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        //如果当前查询到购物车数据中的套餐或者菜品不唯一，说明是在加份数
        if (one != null) {
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
        } else {
            //说明不存在是新添加进购物车的菜品或套餐,num字段设置为1
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    /**
     * 通过前端传回来的dishId构造条件进行删除
     *
     * @param shoppingCart
     * @return
     */
    /*
   'url': '/shoppingCart/sub',
   'method': 'post',
    data
    POST
    http://localhost:8080/shoppingCart/sub
     */
    @PostMapping("/sub")
    public R<ShoppingCart> subDishToShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingCart:{}", shoppingCart);
        //线程id判断当前用户的购物车信息
        Long currentId = BaseContext.getCurrentId();
        //通过dishId或者setmealId判断删除的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();


        //判断是否是一份，一份直接删除，不是一份更新购物车num字段信息
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(currentId != null, ShoppingCart::getUserId, currentId);

        //通过菜品id或套餐id设置条件
        lambdaQueryWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
        lambdaQueryWrapper.eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);


        //拿到当前点击的菜品或套餐实体
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        if (one.getNumber() > 1) {
            one.setNumber(one.getNumber() - 1);
            shoppingCartService.updateById(one);//把当前的数量更新
        } else if (one.getNumber() == 1) {//等于1份，直接删除
            shoppingCartService.remove(lambdaQueryWrapper);
            one = shoppingCart;
        }
        log.info("购物车:{}", one);
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

