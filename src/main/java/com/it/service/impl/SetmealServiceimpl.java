package com.it.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.common.CustomException;
import com.it.dto.SetmealDto;
import com.it.entity.Setmeal;
import com.it.entity.SetmealDish;
import com.it.mapper.Setmealmapper;
import com.it.service.SetmealDishService;
import com.it.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceimpl extends ServiceImpl<Setmealmapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    //新增套餐，同时需要保存套餐和菜品的关联关系

    @Override
    @Transactional(readOnly = false)
    public void savaW(SetmealDto setmealDto) {
        //保存套餐的基本信息。操作setmeal。执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());


        //保存套餐和菜品的关联信息，操作setmeal—dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);

    }

    //删除套餐，多表删除
    @Override
    @Transactional(readOnly = false)
    public void delete(List<Long> ids) {
        //查询套餐状态，是否可以删除
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId, ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(lambdaQueryWrapper);
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //可以删除，先删除套餐表中的数据--setmeal
        this.removeByIds(ids);
        //删除关系表中的数据---setmeal—dish
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(lambdaQueryWrapper1);

    }

    @Override
    public SetmealDto getByIdW(long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);

        setmealDto.setSetmealDishes(list);
        BeanUtils.copyProperties(setmeal, setmealDto);

        return setmealDto;

    }

    @Override
    public void updateW(SetmealDto setmealDto) {
        //修改setmeal表
        this.updateById(setmealDto);
        //先删除原有数据在添加
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(lambdaQueryWrapper);
        //传过来修改以后的数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);


    }


}
