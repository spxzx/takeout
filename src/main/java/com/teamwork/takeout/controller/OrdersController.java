package com.teamwork.takeout.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamwork.takeout.common.R;
import com.teamwork.takeout.dto.OrderDto;
import com.teamwork.takeout.entity.OrderDetail;
import com.teamwork.takeout.entity.Orders;
import com.teamwork.takeout.service.OrderDetailService;
import com.teamwork.takeout.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService ;

    @GetMapping("/userPage")
    public R<Page<OrderDto>> page(Integer page, Integer pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
//        wrapper.like()
        wrapper.orderByDesc("order_time");
        ordersService.page(ordersPage, wrapper);
        BeanUtils.copyProperties(ordersPage, orderDtoPage);
        List<OrderDto> list = ordersPage.getRecords().stream().map((item)->{
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);
            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);
            Integer sumNum = 0;
            if (orderDetails != null) {
                for (OrderDetail orderDetail : orderDetails) {
                    sumNum += orderDetail.getNumber();
                }
            }
            orderDto.setOrderDetails(orderDetails);
            orderDto.setSumNum(sumNum);
            return orderDto;
        }).collect(Collectors.toList());
        orderDtoPage.setRecords(list);
        return R.success(orderDtoPage);
    }

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order) {
        return ordersService.submit(order) ?
                R.success("下单成功！") : R.error("下单失败，请稍后尝试！");
    }

}
