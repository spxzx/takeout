package com.teamwork.takeout.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.lang.reflect.Array;
import java.util.Arrays;

@Slf4j
//
// 异常处理后,将方法的返回值 R 对象转换为json格式的数据,响应放到响应体中,然后渲染给页面
@ResponseBody
// 指定拦截那些类型的控制器
// 所有的调用一般都是从Controller开始的，因此异常最后会抛到Controller ，只需要接受Controller异常即可
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public R<String> doDuplicateKeyExceptionExceptionHandler(DuplicateKeyException ex) {
        log.error("ExceptionInfo: {}", ex.getMessage());
        boolean duplicate_entry = ex.getMessage().contains("Duplicate entry");
        if (duplicate_entry) {
            String[] s = ex.getMessage().split(" ");
            log.info("ArrayObject: {}", s);
            log.info("Array: {}", Arrays.toString(s));
            return R.error("用户名: " + s[9] + "重复");
        }
        return R.error("未知错误!");
    }

    @ExceptionHandler(Exception.class)
    public R<String> doOtherExceptionHandler(Exception ex) {
        log.error(ex.getMessage());
        return R.error("服务器正忙,请稍后...");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }

}
