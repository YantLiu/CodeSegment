package com.alt.enterprise.utils;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @author liuyanting
 * @description
 * @date: 2019/03/29
 */
public class IdGenerator {

    private IdGenerator(){
    }

    /**
     * 自增序列号
     *
     * @param prefix    前缀
     * @param numLength 要生成多少位的数字
     * @return
     */
    public static String SeqGenerator(String prefix, int numLength, RedisTemplate redisTemplate) {
        Long upperCode = null;
        Long size = redisTemplate.opsForList().size(prefix);//查找以prefix作为key值的数据长度
        if (size > 0) {//有数据
            List leve = redisTemplate.opsForList().range(prefix, 0, -1);//获取该key下面的所有值 （-1所有值；1下一个值）
            upperCode = (Long) leve.get(leve.size() - 1);//返回最后一个值
        }
        if (upperCode != null) {//有数据
            upperCode++;//最后的序号加一
        } else {//没有数据
            upperCode = 1L;
        }
        String returnCode = String.format("%0" + numLength + "d", upperCode.longValue());//后缀不够numLength长，前面补充0
        redisTemplate.opsForList().rightPush(prefix, upperCode);//存入Redis
        return returnCode;
    }

}
