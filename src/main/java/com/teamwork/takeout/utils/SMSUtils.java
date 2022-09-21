package com.teamwork.takeout.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;

public class SMSUtils {

    public static void sendMessage(String param) {
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou", "LTAI5tF2gWPtktfRaedoLx8F", "2tmtNy7SbfbgPFS7aoofDStjjOCOXv");
        IAcsClient client = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setSignName("阿里云短信测试");
        request.setTemplateCode("SMS_154950909");
        request.setPhoneNumbers("16673282530");
        request.setTemplateParam("{\"code\":\""+param+"\"}");

        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println("短信发送成功");
        }catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
