package com.teamwork.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamwork.takeout.entity.Orders;

public interface OrdersService extends IService<Orders> {

    public boolean submit(Orders order);

}
