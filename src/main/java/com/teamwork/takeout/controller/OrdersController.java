package com.teamwork.takeout.controller;


import com.teamwork.takeout.common.R;
import com.teamwork.takeout.entity.Orders;
import com.teamwork.takeout.service.OrderDetailService;
import com.teamwork.takeout.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order) {
        return ordersService.submit(order) ?
                R.success("下单成功！") : R.error("下单失败，请稍后尝试！");
    }

}
