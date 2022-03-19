package com.zhou.sharding.config.sharding;

import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author wenyu zhou
 * @version 2022-03-15
 */
@Component
public class UserInfoTableAlgorithm implements PreciseShardingAlgorithm<String> {
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
        if (null == preciseShardingValue || StringUtils.isBlank(preciseShardingValue.getValue())) {
            return null;
        }
        String shardingValue = preciseShardingValue.getValue();
        int index = this.getIndex(shardingValue);
        return collection.stream().filter(userInfo -> userInfo.endsWith(String.valueOf(index))).findFirst().orElse(null);
    }

    /**
     * @param userId 根据userId分表
     * @return 返回[0, 3]内数字
     */
    private int getIndex(String userId) {
        return Math.abs(userId.hashCode()) % 4;
    }
}
