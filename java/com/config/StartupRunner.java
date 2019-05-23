package com.alt.enterprise.config;

import com.alt.enterprise.biz.EnterpriseBiz;
import com.alt.enterprise.constant.ParamsConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author liuyanting
 * @description Spring Boot加载结束时, 初始化数据
 * @date: 2019/04/11
 */
@Slf4j
@Configuration
public class StartupRunner implements ApplicationRunner {
    @Autowired
    private EnterpriseBiz enterpriseBiz;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Spring Boot加载结束, 初始化数据>>>>");
        this.initEnterpriseNumber();
        log.info("Spring Boot加载结束, 初始化数据完成>>>>");
    }

    /**
     * @param
     * @description 初始化企业编号
     * @author liuyanting
     * @date 2019/4/26
     */
    private void initEnterpriseNumber() {
        Long upperCode = redisTemplate.opsForList().size(ParamsConstants.ENTERPRISE_NUMBER);
        Long maxEnterpriseNumber = Long.valueOf(enterpriseBiz.getMaxEnterpriseNumber());
        //若数据库中的最大企业编号>redis最大企业编号, 则将数据库的最大企业编号+1存入redis
        if (maxEnterpriseNumber.compareTo(upperCode) == 1) {
            maxEnterpriseNumber++;
            log.info("更新Redis存放的最大企业编号为>>{}", maxEnterpriseNumber);
            redisTemplate.opsForList().rightPush(ParamsConstants.ENTERPRISE_NUMBER, maxEnterpriseNumber);
        }
    }
}
