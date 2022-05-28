package com.it;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Dish;
import com.it.entity.DishFlavor;
import com.it.entity.Setmeal;
import com.it.service.SetmealService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
class ReggieApplicationTests {

    @Autowired
    private SetmealService setmealService;

    @Test
    void contextLoads() {
        Page<Setmeal> setmealPage = new Page<>(1, 10);
        Page<Setmeal> page = setmealService.page(setmealPage);
        System.out.println("page = " + page);


    }
}
