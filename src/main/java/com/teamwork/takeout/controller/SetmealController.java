package com.teamwork.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamwork.takeout.common.R;
import com.teamwork.takeout.dto.DishDto;
import com.teamwork.takeout.dto.SetmealDishDto;
import com.teamwork.takeout.dto.SetmealDto;
import com.teamwork.takeout.entity.Dish;
import com.teamwork.takeout.entity.Setmeal;
import com.teamwork.takeout.entity.SetmealDish;
import com.teamwork.takeout.service.DishService;
import com.teamwork.takeout.service.SetmealDishService;
import com.teamwork.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;

import java.util.ArrayList;
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
            return R.error("????????????????????? ??????"+ name + "?????????");
        }
        return setmealService.saveSetmealWithDish(setmealDto) ?
                R.success("?????????????????????") : R.error("???????????????????????????????????????");

    }

    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealById(@PathVariable Long id) {
        return R.success(setmealService.getSetmealByIdWithDish(id));
    }

    @PutMapping
    public R<String> updateSetmealWithDish(@RequestBody SetmealDto setmealDto) {
        return setmealService.updateSetmealWithDish(setmealDto) ?
                R.success("?????????????????????") : R.error("???????????????????????????????????????");
    }

    @DeleteMapping
    public R<String> deleteSetmealWithDish(@RequestParam List<Long> ids){
        return setmealService.deleteSetmealWithDish(ids) ?
                R.success("?????????????????????") : R.error("???????????????????????????????????????");
    }

    @PostMapping("/status/0")
    public R<String> updateSetmealStatusToStop(@RequestParam List<Long> ids) {
        List<Setmeal> setmeals = setmealService.listByIds(ids).
                stream().peek((item)->item.setStatus(0)).collect(Collectors.toList());
        return setmealService.updateBatchById(setmeals) ?
                R.success("???????????????????????????") : R.error("?????????????????????????????????????????????");
    }

    @PostMapping("/status/1")
    public R<String> updateSetmealStatusToStart(@RequestParam List<Long> ids) {
        List<Setmeal> setmeals = setmealService.listByIds(ids).
                stream().peek((item)->item.setStatus(1)).collect(Collectors.toList());
        return setmealService.updateBatchById(setmeals) ?
                R.success("???????????????????????????") : R.error("?????????????????????????????????????????????");
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
    public R<List<SetmealDishDto>> getDishById(@PathVariable Long id) {
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        List<SetmealDishDto> setmealDishDtos = new ArrayList<SetmealDishDto>();
        setmealDishes.forEach(setmealDish ->{
            SetmealDishDto setmealDishDto = new SetmealDishDto();
            BeanUtils.copyProperties(setmealDish, setmealDishDto);
            Dish dish = dishService.getById(setmealDish.getDishId());
            setmealDishDto.setImage(dish.getImage());
            setmealDishDto.setDescription(dish.getDescription());
            setmealDishDtos.add(setmealDishDto);
        });
        return R.success(setmealDishDtos);
    }


}
