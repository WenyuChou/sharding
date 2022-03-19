package com.zhou.sharding.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author wenyu zhou
 * @version 2022-03-15
 */
@TableName("user_info")
@Data
public class UserInfo {

    @TableId(type = IdType.INPUT)
    private String userId;
    private String name;
    private int age;
    private int sex;
    private Date createTime;
    private Date updateTime;

}
