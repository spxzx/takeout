package com.teamwork.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamwork.takeout.entity.OrderDetail;
import com.teamwork.takeout.mapper.OrderDetailMapper;
import com.teamwork.takeout.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
