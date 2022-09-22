package com.teamwork.takeout.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.teamwork.takeout.dto.SetmealDto;
import com.teamwork.takeout.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public boolean saveSetmealWithDish(SetmealDto setmealDto);

    public SetmealDto getSetmealByIdWithDish(Long id);

    public boolean updateSetmealWithDish(SetmealDto setmealDto);

    public boolean deleteSetmealWithDish(List<Long> ids);

}
