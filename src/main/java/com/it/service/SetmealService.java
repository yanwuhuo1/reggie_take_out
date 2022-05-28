package com.it.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.it.dto.SetmealDto;
import com.it.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {


    //新增套餐，同时需要保存套餐和菜品的关联关系

    void savaW(SetmealDto setmealDto);


    //删除套餐，多表删除

    void delete(List<Long> ids);

    //回显数据

    SetmealDto getByIdW(long id);

    //修改

    void updateW(SetmealDto setmealDto);
}
