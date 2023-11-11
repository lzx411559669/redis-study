package com.example.redisstudy.redPackageDemo;

import cn.hutool.core.util.IdUtil;
import com.google.common.primitives.Ints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author liuzhengxing
 * @version v1.0
 * @package com.example.redisstudy.redPackageDemo
 * @data 2023/11/10 10:38
 */
@Service
public class RedPackageService {

    public static final String RED_PACKAGE_KEY = "red_package:";

    public static final String RED_PACKAGE_CONSUME_KEY = "red_package_consume:";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送红包
     *
     * @param totalMoney    总金额
     * @param packageNumber 红包个数
     * @return
     */
    public String sendRedPackage(int totalMoney, int packageNumber) {
        //1.拆分红包
        Integer[] splitRedPackages = this.splitRedPackage(totalMoney, packageNumber);

        String key = RED_PACKAGE_KEY + IdUtil.simpleUUID();
        //把红包放进redis list中
        redisTemplate.opsForList().leftPushAll(key, splitRedPackages);
        //设置过期时间为一天
        redisTemplate.expire(key, 1, TimeUnit.DAYS);

        return key + "\t" + Ints.asList(Arrays.asList(splitRedPackages).stream().mapToInt(Integer::valueOf).toArray());

    }

    /**
     * 拆分红包,拆完红包总金额+每个小红包金额别太离谱
     *
     * @param totalMoney    总金额
     * @param packageNumber 红包个数
     * @return
     */
    private Integer[] splitRedPackage(int totalMoney, int packageNumber) {
        //1.创建红包数组
        Integer[] splitRedPackages = new Integer[packageNumber];
        int useMonty = 0;
        Random random = new Random();
        //4.循环创建红包
        for (int i = 0; i < packageNumber; i++) {
            if (i == packageNumber - 1) {
                splitRedPackages[i] = totalMoney - useMonty;
            } else {
                int avgMoney = (totalMoney - useMonty) / (packageNumber - i) * 2;
                splitRedPackages[i] = random.nextInt(avgMoney - 1) + 1;
            }
            useMonty += splitRedPackages[i];
        }
        return splitRedPackages;
    }

    /**
     * 抢红包
     *
     * @param redPackageKey 红包key
     * @param userId        用户id
     * @return
     */
    public String robRedPackage(String redPackageKey, String userId) {
        //1.判断用户是否抢过红包
        String key = RED_PACKAGE_CONSUME_KEY + redPackageKey;
        Object redPackage = redisTemplate.opsForHash().get(key, userId);
        //2.如果没有抢过
        if (null == redPackage) {
            //3.获取红包
            Object partRedPackage = redisTemplate.opsForList().leftPop(RED_PACKAGE_KEY+redPackageKey);
            if (null != partRedPackage) {
                //4.把红包放进redis hash中
                redisTemplate.opsForHash().put(key, userId, partRedPackage);
                return "抢到红包了，金额为：" + partRedPackage;
            }
            return "红包已被抢光了";
        }
        return "您已经抢过红包啦";
    }
}
