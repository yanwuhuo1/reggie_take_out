package com.it.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.common.R;
import com.it.dto.DishDto;
import com.it.dto.SetmealDto;
import com.it.entity.Category;

import com.it.entity.Setmeal;

import com.it.service.CategoryService;
import com.it.service.SetmealDishService;
import com.it.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//套餐管理
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private ApplicationContext applicationContext;

    //套餐管理
    @Autowired
    private SetmealService setmealService;

    //菜品管理

    @Autowired
    private CategoryService categoryService;

    //显示套餐管理数据//套餐分页查询
    @GetMapping("/page")
    public R<Page> querySetmealDish(int page, int pageSize, String name) {

        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        Page<SetmealDto> setmealDtoPage = new Page<>();

        QueryWrapper<Setmeal> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name), "name", name);
        setmealService.page(pageInfo, wrapper);

        List<Setmeal> records = pageInfo.getRecords();

        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");

        List<SetmealDto> setmealDtoList = records.stream().map(item -> {

            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if (category != null) {

                setmealDto.setCategoryName(category.getName());
            }

            return setmealDto;

        }).collect(Collectors.toList());


        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }

    //套餐管理/状态修改

    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> update(@PathVariable("status") int status, Long[] ids) {
        System.out.println("status = " + status);
        System.out.println("ids = " + ids);

        for (int i = 0; i < ids.length; i++) {
            long id = ids[i];

            Setmeal byId = setmealService.getById(id);
            byId.setStatus(status);
            setmealService.updateById(byId);

        }

        return R.success("状态修改成功");
    }

    //套餐管理//新增套餐
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        System.out.println("setmealDish = " + setmealDto);
        setmealService.savaW(setmealDto);
        return R.success("新增套餐成功");
    }

    //删除套餐，批量删除
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.delete(ids);
        return R.success("删除成功");
    }

    //修改//回显数据
    @GetMapping("{id}")
    public R<Setmeal> getById(@PathVariable long id) {
        SetmealDto byIdW = setmealService.getByIdW(id);
        if (byIdW != null) {
            return R.success(byIdW);
        }

        return R.error("数据显示错误");

    }

    //套餐管理修改数据
    @PutMapping
    public R<String> upload(@RequestBody SetmealDto setmealDto) {

        setmealService.updateW(setmealDto);
        return R.success("更新套餐成功");
    }

    //移动端套餐显示,根据条件查询套餐数据
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return R.success(list);
    }

}
