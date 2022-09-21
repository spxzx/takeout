package com.teamwork.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamwork.takeout.common.R;
import com.teamwork.takeout.dto.DishDto;
import com.teamwork.takeout.entity.Category;
import com.teamwork.takeout.entity.Dish;
import com.teamwork.takeout.service.CategoryService;
import com.teamwork.takeout.service.DishFlavorService;
import com.teamwork.takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        wrapper.like(name!=null,"name", name);
        wrapper.orderByDesc("update_time");
        dishService.page(dishPage, wrapper);
        // 对象拷贝                    records 结果列表
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto) {
        String name = dishDto.getName();
        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        wrapper.eq(name!=null,"name",name);
        if (dishService.getOne(wrapper) != null){
            return R.error("添加菜品失败， 菜品"+ name + "已存在");
        }
        return dishService.saveWithFlavor(dishDto) ?
                R.success("菜品添加成功！") : R.error("菜品添加失败，请稍后重试！");
    }

    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable Long id) {
        return R.success(dishService.getByIdWithFlavor(id));
    }

    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto) {
        return dishService.updateWithFlavor(dishDto) ?
                R.success("修改菜品成功！") : R.error("修改菜品失败，请稍后重试！");
    }

    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids) {
        return dishService.removeWithFlavor(ids) ?
                R.success("删除菜品成功！") : R.error("删除菜品失败，请稍后重试！");
    }

    @PostMapping("/status/0")
    public R<String> updateDishStatusToStop(@RequestParam List<Long> ids) {
        List<Dish> list = dishService.listByIds(ids).
                stream().peek((item)->item.setStatus(0)).collect(Collectors.toList());
        return dishService.updateBatchById(list) ?
                R.success("菜品状态修改成功！") : R.error("菜品状态修改失败，请稍后重试！");
    }

    @PostMapping("/status/1")
    public R<String> updateDishStatusToStart(@RequestParam List<Long> ids) {
        List<Dish> list = dishService.listByIds(ids).stream().peek((item)->item.setStatus(1)).collect(Collectors.toList());
        return dishService.updateBatchById(list) ?
                R.success("菜品状态修改成功！") : R.error("菜品状态修改失败，请稍后重试！");
    }

    @GetMapping("/list")
    public R<List<Dish>> getDishList(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        return R.success(dishService.list(queryWrapper));
    }

}
