package com.teamwork.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamwork.takeout.common.R;
import com.teamwork.takeout.entity.Category;
import com.teamwork.takeout.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page<Category>> page(Integer page, Integer pageSize, String name) {
        Page<Category> categoryPage = new Page<>(page, pageSize);
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");
        categoryService.page(categoryPage, wrapper);
        return R.success(categoryPage);
    }

    @PostMapping
    public R<String> addCategory(@RequestBody Category category) {
        String name = category.getName();
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq(name!=null,"name",name);
        if (categoryService.getOne(wrapper) != null) {
            return R.error("添加分类失败，分类" + name + "已存在!");
        }
        return categoryService.save(category) ?
                R.success("分类添加成功！") : R.error("分类添加失败，请稍后再试！");
    }

    @PutMapping
    public R<String> updateCategory(@RequestBody Category category) {
        return categoryService.updateById(category) ?
                R.success("修改分类成功！") : R.error("修改分类失败！");
    }

    @DeleteMapping
    public R<String> deleteCategory(Long id) {
        return categoryService.remove(id) ?
                R.success("删除分类成功！") : R.error("删除分类失败！");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        return R.success(categoryService.list(queryWrapper));
    }

}
