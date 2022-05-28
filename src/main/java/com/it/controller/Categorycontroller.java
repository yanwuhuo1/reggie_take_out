package com.it.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.common.R;
import com.it.entity.Category;
import com.it.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

//分类管理
@RestController
@RequestMapping("/category")
public class Categorycontroller {
    @Autowired
    private CategoryService categoryService;

    //添加菜品分类
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("添加成功");
    }

    //分类管理菜品显示
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        //构造分页构造器
        Page<Category> page1 = new Page<>(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Category::getSort);
        //执行查询
        categoryService.page(page1, lambdaQueryWrapper);
        return R.success(page1);

    }

    //修改菜品消息
    @PutMapping
    public R<String> update(@RequestBody Category category) {

        categoryService.updateById(category);
        return R.success("修改成功");

    }

    //根据id删除菜品分类
    @DeleteMapping()
    public R<String> delete(long ids) {

        categoryService.remove(ids);
        return R.success("删除成功");
    }

    //根据条件来查询分类数据
    @GetMapping("/list")
    public R<List<Category>> listR(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getSort);
        //查询
        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return R.success(list);
    }

}
