package com.teamwork.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamwork.takeout.common.BaseContext;
import com.teamwork.takeout.common.CustomException;
import com.teamwork.takeout.entity.*;
import com.teamwork.takeout.mapper.OrdersMapper;
import com.teamwork.takeout.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public boolean submit(Orders order) {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> cartList = shoppingCartService.list(queryWrapper);
        if (cartList == null || cartList.size() == 0) {
            throw new CustomException("购物车为空，不能下单！");
        }
        User user = userService.getById(userId);
        Long addressId = order.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressId);
        if (addressBook == null) {
            throw new CustomException("用户地址信息有误，无法下单！");
        }
        Long orderId = IdWorker.getId();
        AtomicInteger atomic = new AtomicInteger(0);
        List<OrderDetail> orderDetails = cartList.stream().map((item)->{
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrderId(orderId);
                    orderDetail.setNumber(item.getNumber());
                    orderDetail.setDishFlavor(item.getDishFlavor());
                    orderDetail.setDishId(item.getDishId());
                    orderDetail.setSetmealId(item.getSetmealId());
                    orderDetail.setName(item.getName());
                    orderDetail.setImage(item.getImage());
                    orderDetail.setAmount(item.getAmount());
                    atomic.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
                    return orderDetail;
                }).collect(Collectors.toList());
        order.setId(orderId);
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setStatus(2);
        order.setAmount(new BigDecimal(atomic.get()));
        order.setUserId(userId);
        order.setNumber(String.valueOf(orderId));
        order.setUserName(user.getName());
        order.setConsignee(addressBook.getConsignee());
        order.setPhone(addressBook.getPhone());
        order.setAddress((
                   addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail())
        );
        this.save(order);
        orderDetailService.saveBatch(orderDetails);
        return shoppingCartService.remove(queryWrapper);
    }

}
