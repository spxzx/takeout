package com.teamwork.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamwork.takeout.entity.AddressBook;
import com.teamwork.takeout.mapper.AddressBookMapper;
import com.teamwork.takeout.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
