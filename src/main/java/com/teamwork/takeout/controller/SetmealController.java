package com.teamwork.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamwork.takeout.common.R;
import com.teamwork.takeout.dto.DishDto;
import com.teamwork.takeout.dto.SetmealDto;
import com.teamwork.takeout.entity.Dish;
import com.teamwork.takeout.entity.Setmeal;
import com.teamwork.takeout.service.DishService;
import com.teamwork.takeout.service.SetmealDishService;
import com.teamwork.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @GetMapping("/page")
    public R<Page<Setmeal>> page(Integer page, Integer pageSize, String name){
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, queryWrapper);
        return R.success(setmealPage);
    }

    @PostMapping
    public R<String> addSetmeal(@RequestBody SetmealDto setmealDto){
        String name = setmealDto.getName();
        QueryWrapper<Setmeal> wrapper = new QueryWrapper<>();
        wrapper.eq(name!=null, "name", name);
        if (setmealService.getOne(wrapper) != null) {
            return R.error("添加套餐失败， 套餐"+ name + "已存在");
        }
        return setmealService.saveSetmealWithDish(setmealDto) ?
                R.success("套餐添加成功！") : R.error("套餐添加失败，请稍后重试！");

    }

    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealById(@PathVariable Long id) {
        return R.success(setmealService.getSetmealByIdWithDish(id));
    }

    @PutMapping
    public R<String> updateSetmealWithDish(@RequestBody SetmealDto setmealDto) {
        return setmealService.updateSetmealWithDish(setmealDto) ?
                R.success("套餐修改成功！") : R.error("套餐修改失败，请稍后重试！");
    }

    @DeleteMapping
    public R<String> deleteSetmealWithDish(@RequestParam List<Long> ids){
        return setmealService.deleteSetmealWithDish(ids) ?
                R.success("套餐删除成功！") : R.error("套餐删除失败，请稍后重试！");
    }

    @PostMapping("/status/0")
    public R<String> updateSetmealStatusToStop(@RequestParam List<Long> ids) {
        List<Setmeal> setmeals = setmealService.listByIds(ids).
                stream().peek((item)->item.setStatus(0)).collect(Collectors.toList());
        return setmealService.updateBatchById(setmeals) ?
                R.success("套餐状态修改成功！") : R.error("套餐状态修改失败，请稍后尝试！");
    }

    @PostMapping("/status/1")
    public R<String> updateSetmealStatusToStart(@RequestParam List<Long> ids) {
        List<Setmeal> setmeals = setmealService.listByIds(ids).
                stream().peek((item)->item.setStatus(1)).collect(Collectors.toList());
        return setmealService.updateBatchById(setmeals) ?
                R.success("套餐状态修改成功！") : R.error("套餐状态修改失败，请稍后尝试！");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    @GetMapping("/dish/{id}")
    public R<DishDto> getDishById(@PathVariable Long id){
        return R.success(dishService.getByIdWithFlavor(id));
    }

}
