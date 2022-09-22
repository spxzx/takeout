package com.teamwork.takeout.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamwork.takeout.common.BaseContext;
import com.teamwork.takeout.common.R;
import com.teamwork.takeout.dto.OrderDto;
import com.teamwork.takeout.entity.*;
import com.teamwork.takeout.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private UserService userService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService ;

    @GetMapping("/page")
    public R<Page<OrderDto>> page(
            Integer page, Integer pageSize, String number,
            @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
            @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.like(number!=null,"number",number);
        wrapper.between(beginTime!=null&&endTime!=null,"order_time", beginTime,endTime);
        wrapper.orderByDesc("order_time");
        ordersService.page(ordersPage, wrapper);
        BeanUtils.copyProperties(ordersPage, orderDtoPage);
        List<OrderDto> list = ordersPage.getRecords().stream().map((item)->{
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);
            User user = userService.getById(item.getUserId());
            orderDto.setUserName(user.getName());
            AddressBook addressBook = addressBookService.getById(item.getAddressBookId());
            orderDto.setAddress(addressBook.getDetail());
            return orderDto;
        }).collect(Collectors.toList());
        orderDtoPage.setRecords(list);
        return R.success(orderDtoPage);
    }

    @GetMapping("/userPage")
    public R<Page<OrderDto>> userPage(Integer page, Integer pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
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

    @PutMapping
    public R<String> updateStatus(@RequestBody Orders order){
        return ordersService.updateById(order) ?
                R.success("派送成功！") : R.error("派送失败，请稍后重试！");
    }

    @PostMapping("/again")
    @Transactional
    public R<String> again(@RequestBody Orders order) {
        order = ordersService.getById(order.getId());
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, order.getId());
        List<OrderDetail> detailList = orderDetailService.list(queryWrapper);
        for (OrderDetail orderDetail : detailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setName(orderDetail.getName());
            if (orderDetail.getDishId() != null) {
                Dish dish = dishService.getById(orderDetail.getDishId());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setDishId(orderDetail.getDishId());
            } else {
                Setmeal setmeal = setmealService.getById(orderDetail.getSetmealId());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setSetmealId(orderDetail.getSetmealId());
            }
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setAmount(orderDetail.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }
        return R.success("再来一单！");
    }

}
