package com.it.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.Dish;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
