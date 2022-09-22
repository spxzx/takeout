package com.teamwork.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamwork.takeout.common.CustomException;
import com.teamwork.takeout.dto.SetmealDto;
import com.teamwork.takeout.entity.Setmeal;
import com.teamwork.takeout.entity.SetmealDish;
import com.teamwork.takeout.mapper.SetmealMapper;
import com.teamwork.takeout.service.SetmealDishService;
import com.teamwork.takeout.service.SetmealService;
import jdk.nashorn.internal.runtime.ListAdapter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public boolean saveSetmealWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().peek((item)->item.setSetmealId(setmealId)).collect(Collectors.toList());
        return setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public SetmealDto getSetmealByIdWithDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(this.getById(id), setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    @Override
    @Transactional
    public boolean updateSetmealWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().
                stream().peek((item)->
                        item.setSetmealId(setmealDto.getId())).collect(Collectors.toList());
        return setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public boolean deleteSetmealWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        if (this.count(setmealLambdaQueryWrapper) > 0) {
            throw new CustomException("选择了正在出售中套餐，不能删除！");
        }
        this.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        return setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }

}
