package com.teamwork.takeout.utils;

import com.teamwork.takeout.common.R;

import java.util.Random;

public class ValidateCodeUtils {

    public static Integer generateValidateCode(int length) {
        Integer code = null;
        if (length == 4) {
            code = new Random().nextInt(9999);
            if (code < 1000) {
                code += 1000;
            }
        } else if (length == 6) {
            code = new Random().nextInt(999999);
            if (code < 100000) {
                code += 100000;
            }
        } else {
          throw new RuntimeException("只能生产4位或6位数字验证码！");
        }
        return code;
    }

    public static String generateValidateCode4String(int length) {
        Random rdm = new Random();
        String hash1 = Integer.toHexString(rdm.nextInt());
        String capstr = hash1.substring(0, length);
        return capstr;
    }

}
