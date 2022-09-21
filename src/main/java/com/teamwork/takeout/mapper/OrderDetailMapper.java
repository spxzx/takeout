package com.teamwork.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamwork.takeout.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
