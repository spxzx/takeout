package com.teamwork.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.teamwork.takeout.common.R;
import com.teamwork.takeout.entity.User;
import com.teamwork.takeout.service.UserService;
import com.teamwork.takeout.utils.SMSUtils;
import com.teamwork.takeout.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if (!StringUtils.isNullOrEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
//            SMSUtils.sendMessage(code);
            session.setAttribute(phone, code);
            log.info("验证码: {}", code);
            return R.success("手机验证码发送成功！");
        }
        return R.error("短信发送失败，请稍后重新尝试！");
    }

    @PostMapping("/login")
    public R<User> login(HttpServletRequest req, @RequestBody Map map) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        Object codeInSession = req.getSession().getAttribute(phone);
        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            req.getSession().setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败，请稍后重新尝试！");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest req) {
        req.getSession().removeAttribute("user");
        return R.success("退出成功...");
    }

}
