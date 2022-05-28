package com.it.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.it.dto.DishDto;
import com.it.entity.Dish;



/**
 * @author 28392
 */
public interface DishService extends IService<Dish> {
/*
    新增菜品，同时插入菜品对应的口味数据，需要同时操作两张表，dish,dish_flavor
*/

    public void saveWishFlavor(DishDto dishDto);



    //根据id查询菜品信息和对应的口味消息

    public DishDto getByIdW(Long id);

    //更新菜品信息，更新口味信息

    public void updateW(DishDto dishDto);

}
