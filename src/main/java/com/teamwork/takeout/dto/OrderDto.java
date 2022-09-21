package com.teamwork.takeout.dto;

import com.teamwork.takeout.entity.OrderDetail;
import com.teamwork.takeout.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {

    private List<OrderDetail> orderDetails;

    private Integer sumNum;

}
