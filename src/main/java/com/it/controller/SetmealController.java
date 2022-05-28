package com.it.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.common.R;
import com.it.dto.DishDto;
import com.it.dto.SetmealDto;
import com.it.entity.Category;

import com.it.entity.Setmeal;

import com.it.service.CategoryService;
import com.it.service.SetmealDishService;
import com.it.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//套餐管理
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    //套餐管理
    @Autowired
    private SetmealService setmealService;

    //菜品管理

    @Autowired
    private CategoryService categoryService;

    //显示套餐管理数据//套餐分页查询
    @GetMapping("/page")
    private R<Page> sava(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //分页构造器对象
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //模糊查询
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件，根据更新时间降序排列
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        try {
            Page<Setmeal> page1 = setmealService.page(setmealPage);
            System.out.println("page1 = " + page1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {


            BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
            List<Setmeal> records = setmealPage.getRecords();
            List<SetmealDto> collect = records.stream().map((item) -> {
                SetmealDto setmealDto = new SetmealDto();
                //对象拷贝
                BeanUtils.copyProperties(item, setmealDto);
                //分类id
                Long categoryId = item.getCategoryId();
                //根据分类id查询
                Category byId = categoryService.getById(categoryId);
                if (byId != null) {
                    //分类名称
                    String name1 = byId.getName();
                    setmealDto.setCategoryName(name1);

                }
                return setmealDto;

            }).collect(Collectors.toList());
            //对象拷贝

            setmealDtoPage.setRecords(collect);

            return R.success(setmealDtoPage);
        }
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
