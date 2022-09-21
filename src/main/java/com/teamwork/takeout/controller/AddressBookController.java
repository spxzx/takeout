package com.teamwork.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.teamwork.takeout.common.BaseContext;
import com.teamwork.takeout.common.R;
import com.teamwork.takeout.entity.AddressBook;
import com.teamwork.takeout.entity.User;
import com.teamwork.takeout.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    private R<String> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        return addressBookService.save(addressBook) ?
                R.success("地址添加成功！") : R.error("地址添加失败，请稍后重试！");
    }

    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null!=addressBook.getUserId(),
                AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        return R.success(addressBookService.list(queryWrapper));
    }

    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id) {
        return R.success(addressBookService.getById(id));
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        return addressBookService.updateById(addressBook) ?
                R.success("保存成功！") : R.error("保存失败，请稍后重试！");
    }

    @DeleteMapping
    public R<String> remove(@RequestParam Long ids){
        return addressBookService.removeById(ids) ?
                R.success("删除成功！") : R.error("删除失败，请稍后重试！");
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (addressBook == null) {
            return R.error("没有找到该对象！");
        }else {
            return R.success(addressBook);
        }
    }

    @PutMapping("/default")
    @Transactional
    public R<AddressBook> updateDefault(@RequestBody AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(queryWrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

}
