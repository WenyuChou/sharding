package com.zhou.sharding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zhou.sharding.entity.UserInfo;
import com.zhou.sharding.mapper.UserInfoMapper;
import com.zhou.sharding.vo.ApiResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author wenyu zhou
 * @version 2022-03-15
 */
@RestController
@RequestMapping("/sharding")
public class ShardingController {
    @Resource
    private UserInfoMapper userInfoMapper;

    @PostMapping("/getUser/{userId}")
    public ApiResult getUser(@PathVariable("userId") String userId){
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUserId,userId);
        return ApiResult.success(userInfoMapper.selectOne(queryWrapper));
    }

    @PostMapping("/insert")
    public ApiResult insert(@RequestBody UserInfo user){
        user.setCreateTime(new Date());
        userInfoMapper.insert(user);
        return ApiResult.success(user);
    }

    @PostMapping("/update")
    public ApiResult update(@RequestBody UserInfo user){
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<UserInfo>()
                .eq(UserInfo::getUserId,user);
        userInfoMapper.update(user,updateWrapper);
        return ApiResult.success();
    }
    @PostMapping("/delete/{userId}")
    public ApiResult delete(@PathVariable("userId")String userId){
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUserId,userId);
        userInfoMapper.delete(queryWrapper);
        return ApiResult.success();
    }
    @PostMapping("/listAll")
    public ApiResult listAll(){
        List<UserInfo> list = userInfoMapper.selectList(new QueryWrapper<>());
        return ApiResult.success(list);
    }
}
