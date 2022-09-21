package com.teamwork.takeout.dto;

import com.teamwork.takeout.entity.SetmealDish;
import lombok.Data;

@Data
public class SetmealDishDto extends SetmealDish {

    private String image;

    private String description;

}
