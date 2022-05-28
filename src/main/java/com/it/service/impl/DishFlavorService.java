package com.it.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.entity.DishFlavor;
import com.it.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorService extends ServiceImpl<DishFlavorMapper, DishFlavor> implements com.it.service.DishFlavorService {
}
