package com.teamwork.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamwork.takeout.entity.User;
import com.teamwork.takeout.mapper.UserMapper;
import com.teamwork.takeout.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
