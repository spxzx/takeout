package com.teamwork.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamwork.takeout.common.CustomException;
import com.teamwork.takeout.dto.DishDto;
import com.teamwork.takeout.entity.Dish;
import com.teamwork.takeout.entity.DishFlavor;
import com.teamwork.takeout.mapper.DishMapper;
import com.teamwork.takeout.service.DishFlavorService;
import com.teamwork.takeout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public boolean saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().peek((item)-> item.setDishId(dishId)).collect(Collectors.toList());
        return dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    public boolean updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().peek((item)-> item.setDishId(dishDto.getId())).collect(Collectors.toList());
        return dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public boolean removeWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId, ids);
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
        if (this.count(dishLambdaQueryWrapper) > 0) {
            throw new CustomException("选择了正在出售中菜品，不能删除！");
        }
        boolean dishFlag = this.removeByIds(ids);
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId, ids);
        boolean dishFlavorFlag = dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        return dishFlag && dishFlavorFlag;
    }

}
