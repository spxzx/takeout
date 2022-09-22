package com.teamwork.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamwork.takeout.dto.DishDto;
import com.teamwork.takeout.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    public boolean saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public boolean updateWithFlavor(DishDto dishDto);

    public boolean removeWithFlavor(List<Long> ids);

}
