package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
