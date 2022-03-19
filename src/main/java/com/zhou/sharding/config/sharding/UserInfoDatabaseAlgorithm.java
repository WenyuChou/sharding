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
public class UserInfoDatabaseAlgorithm implements PreciseShardingAlgorithm<String> {
    /**
     *
     * @param collection Collection<String> 参数在几种分片策略中使用一致，在分库时值为所有分片库的集合 databaseNames，分表时为对应分片库中所有分片表的集合 tablesNames
     * @param preciseShardingValue 为分片属性，其中 logicTableName 为逻辑表，columnName 分片健（字段），value 为从 SQL 中解析出的分片健的值
     * @return
     */
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
     * @return 返回[0, 1]内数字
     */
    private int getIndex(String userId) {
        return Math.abs(userId.hashCode()) % 2;
    }
}
