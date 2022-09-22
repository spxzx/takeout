package com.teamwork.takeout.dto;

import com.teamwork.takeout.entity.Setmeal;
import com.teamwork.takeout.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

}
