package com.it.dto;


import com.it.entity.Setmeal;
import com.it.entity.SetmealDish;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes=new ArrayList<>();

    private String categoryName;
}
