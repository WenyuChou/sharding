package com.zhou.sharding.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zhou.sharding.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * @author wenyu zhou
 * @version 2022-03-15
 */
@Slf4j
public class Test {
    public static void main(String[] args) {
        new Test().get();
    }

    public void insert() {
        String url = "http://127.0.0.1:8080/sharding/insert";
        String userId = UUID.randomUUID().toString().replace("-", "");
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setName("kiki");
        userInfo.setAge(18);
        userInfo.setSex(1);
        String[] posts = HttpClient.post(url, JSON.toJSONString(userInfo), 5);
        log.info(posts[0]);
        log.info(posts[1]);
        System.out.println(userId);
        System.out.println(Math.abs(userInfo.getUserId().hashCode()) % 4);
    }

    public void get() {
        String url = "http://127.0.0.1:8080/sharding/getUser/91aa59fa9b04417798e39f1810edc29a";
        String[] posts = HttpClient.post(url, JSON.toJSONString(""), 5);
        log.info(posts[0]);
        log.info(posts[1]);
        log.info(JSON.toJSONString(JSON.parseObject(posts[1]), true));
    }
    public void getAll() {
        String url = "http://127.0.0.1:8080/sharding/listAll";
        String[] posts = HttpClient.post(url, JSON.toJSONString(""), 5);
        log.info(posts[0]);
        log.info(posts[1]);
        log.info(JSON.toJSONString(JSON.parseObject(posts[1]), true));
    }
}
