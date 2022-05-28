package com.it.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.common.R;
import com.it.dto.DishDto;
import com.it.entity.Category;
import com.it.entity.Dish;
import com.it.entity.DishFlavor;
import com.it.entity.Employee;
import com.it.service.CategoryService;
import com.it.service.DishFlavorService;
import com.it.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.Request;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
//菜品管理显示

/**
 * @author 28392
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class Dishcontroller {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    //菜品管理显示//查询菜品名称

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //添加排序条件//降序
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo, lambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> collect = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类名称
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                String name1 = byId.getName();
                dishDto.setCategoryName(name1);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishPage.setRecords(collect);

        return R.success(dishPage);
    }


    //批量起售，批量停售，停售，起售

    @PostMapping("/status/{status}")
    public R<String> update(@PathVariable("status") int status, Long[] ids) {
        System.out.println("status = " + status);
        System.out.println("ids = " + ids);

        for (int i = 0; i < ids.length; i++) {
            long id = ids[i];

            Dish byId = dishService.getById(id);
            byId.setStatus(status);
            dishService.updateById(byId);

        }

        return R.success("状态修改成功");
    }

    //添加

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWishFlavor(dishDto);
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("新增菜品成功");
    }

    //多表查寻、、根据id查询菜品消息对应的口味消息
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto byId = dishService.getByIdW(id);
        return R.success(byId);

    }

    //修改并保存菜品信息

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateW(dishDto);
        //清理页面菜品，套餐，显示的缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return R.success("更新菜品成功");
    }

    //删除，批量删除

    @DeleteMapping
    public R<String> delete(Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            Long id = ids[i];
            dishService.removeById(id);

        }
        return R.success("删除成功");
    }

    /*
     *套餐管理//套餐餐品//根据条件查询菜品数据
     * */

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = null;
        //动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList != null) {
            //如果存在，直接返回，不从数据库查询
            return R.success(dishDtoList);
        }



        //构造条件查询
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        lambdaQueryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类名称
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                String name1 = byId.getName();
                dishDto.setCategoryName(name1);
            }
            //当前菜品菜品id
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(DishFlavor::getDishId, id);
            List<DishFlavor> list1 = dishFlavorService.list(lambdaQueryWrapper1);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }


}
