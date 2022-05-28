package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.dto.DishDto;
import com.it.entity.Dish;
import com.it.entity.DishFlavor;
import com.it.mapper.DishMapper;
import com.it.service.DishFlavorService;
import com.it.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class DishServiceimpl extends ServiceImpl<DishMapper, Dish> implements DishService {



    @Autowired
    private DishFlavorService dishFlavorService;

/*    //新增菜品，同时保存对应的口味数据*/
    @Override
    @Transactional(readOnly = false)
    public void saveWishFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品表dish

        this.save(dishDto);
        Long id = dishDto.getId();/*//菜品id*/
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavors
        dishFlavorService.saveBatch(flavors);
    }

    //根据id查询菜品信息和对应的口味消息

    @Override
    public DishDto getByIdW(Long id) {
        //查询菜品基本信息，从对应dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();


        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());

        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(list);

        BeanUtils.copyProperties(dish,dishDto);

        return dishDto;

    }

    @Override
    public void updateW(DishDto dishDto) {
        //更新dish表的基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味信息数据--dish-flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);

        //添加当前用户提交过来的口味信息--dish-flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

}
