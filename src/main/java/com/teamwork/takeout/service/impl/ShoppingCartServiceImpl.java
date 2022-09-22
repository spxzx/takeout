package com.teamwork.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamwork.takeout.entity.ShoppingCart;
import com.teamwork.takeout.mapper.ShoppingCartMapper;
import com.teamwork.takeout.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
