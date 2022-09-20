package com.teamwork.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamwork.takeout.entity.Category;

public interface CategoryService extends IService<Category> {

    public boolean remove(Long id);

}
