package com.it.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.common.CustomException;
import com.it.entity.Category;
import com.it.entity.Dish;
import com.it.entity.Setmeal;
import com.it.mapper.CategoryMapper;
import com.it.service.CategoryService;
import com.it.service.DishService;
import com.it.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceimpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
/*
*
* 根据id删除分类，删除之前需要进行判断
* */
    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        lambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count = dishService.count(lambdaQueryWrapper);
        //查询当前分类是否关联菜品，如果关联，抛出一个业务异常
        if (count>0){
            //已经关联菜品。抛出业务异常
            throw new CustomException("当前分类关联了菜品，不能删除");
        }

        //查询当前分类是否关联套餐，如果关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId,ids);
        int count1 = setmealService.count(lambdaQueryWrapper1);
        if (count1>0){
            //已经关联套餐。抛出业务异常
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        //正常删除
        super.removeById(ids);
    }
}
