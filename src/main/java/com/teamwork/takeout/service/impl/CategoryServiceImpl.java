package com.teamwork.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamwork.takeout.common.CustomException;
import com.teamwork.takeout.entity.Category;
import com.teamwork.takeout.entity.Dish;
import com.teamwork.takeout.entity.Setmeal;
import com.teamwork.takeout.mapper.CategoryMapper;
import com.teamwork.takeout.service.CategoryService;
import com.teamwork.takeout.service.DishService;
import com.teamwork.takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public boolean remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        if (dishService.count(dishLambdaQueryWrapper) > 0) {
            throw new CustomException("当前分类下关联了菜品，不能删除！");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        if (setmealService.count(setmealLambdaQueryWrapper) > 0) {
            throw new CustomException("当前分类下关联了菜品，不能删除！");
        }
        return super.removeById(id);
    }

}
