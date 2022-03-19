package com.zhou.sharding.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wenyu zhou
 * @version 2022-03-19
 * 雪花算法
 * 1bit + 41位的时间戳 + 5位业务线ID + 7位机器ID + 10位序列号
 * 1. 得到组成部分
 * 2. 得到组成部分的长度，得到左位移的长度
 * 3. 套公式
 * 4. 序列号，同一毫秒同机器的时候递增
 */
public class SnowFlakeIdService {
    /**
     * 系统上线时间
     */
    private final long startTime = 1647693796749L;
    //业务线ID
    private long businessId;
    //机器ID
    private long workId;
    //序列号
    private long serialNum = 0;

    public SnowFlakeIdService(long businessId, long workId) {
        this.businessId = businessId;
        this.workId = workId;
    }

    //得到左位移数
    private final long serialNumBits = 10L;
    private final long workIdBits = 7L;
    private final long businessIdBits = 5L;

    //得到左位移长度
    private final long workIdShift = serialNumBits;
    private final long businessIdShift = workIdShift + workIdBits;
    private final long timeStampShift = businessIdShift + businessIdBits;

    /**
     * 上次执行的时间
     */
    private long lastTimeStamp = 0L;
    /**
     * 序列号的最大值
     */
    private final long serialNumMax = -1L ^ (-1L << serialNumBits);

    public synchronized long getId() {
        long timeStamp = System.currentTimeMillis();
        serialNum = (serialNum + 1) & serialNumMax;
        if (timeStamp == lastTimeStamp) {
            if (serialNum == 0) {
                timeStamp = waitNextMillis(timeStamp);
            }
        } else {
            //若序列号不变，有弊端，会分布不均匀 serialNum = 0;
            serialNum = timeStamp & 1;
        }
        lastTimeStamp = timeStamp;
        return ((timeStamp - startTime) << timeStampShift)
                | (businessId << businessIdShift)
                | (workId << workIdShift)
                | serialNum;
    }

    private long waitNextMillis(long timeStamp) {
        long nowTimeStamp = System.currentTimeMillis();
        while (timeStamp >= nowTimeStamp) {
            nowTimeStamp = System.currentTimeMillis();
        }
        return nowTimeStamp;
    }

    public static void main(String[] args) throws Exception{
        SnowFlakeIdService service = new SnowFlakeIdService(1, 1);
        List<Long> longs = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1L);
            longs.add(service.getId());
        }
        long l = System.currentTimeMillis() - start;
        longs.forEach(System.out::println);
        System.out.println("耗费时间：" + l);
    }
}
