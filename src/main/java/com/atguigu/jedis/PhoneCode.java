package com.atguigu.jedis;

import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * @author Lenovo
 */
public class PhoneCode {
    public static void main(String[] args) {
        verifyCode("15286878832");
        CheckCode("15286878832","930166");
    }

    public static String getPhoneCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6;i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public static void verifyCode(String Phone){

        Jedis jedis = new Jedis("192.168.163.131",6379);
        jedis.auth("yu149286");

        String countKey = "VerifyCode"+Phone+":count";
        String codeKey = "VerifyCode"+Phone+":code";

        String count = jedis.get("countKey");
        if (count == null) {
            jedis.setex(countKey,24*60*60,"1");
        } else if (Integer.parseInt(count)<=2) {
            jedis.incr(countKey);
        } else {
            System.out.println("您已发送3次,今日无法再次发送");
            jedis.close();
            return;
        }

        String vCode = getPhoneCode();
        jedis.setex(codeKey,60*2,vCode);
        jedis.close();
    }

    public static void CheckCode(String Phone,String Code){

        Jedis jedis = new Jedis("192.168.163.131",6379);
        jedis.auth("yu149286");

        String codeKey = "VerifyCode"+Phone+":code";
        String redisCode = jedis.get(codeKey);

        if (redisCode != null) {
            if (redisCode.equals(Code)) {
                System.out.println("验证成功!!");
            } else {
                System.out.println("验证失败!!");
            }
        } else {
            System.out.println("您还未输入验证码");
        }
        jedis.close();
    }
}
