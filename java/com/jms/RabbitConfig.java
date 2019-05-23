package com.alt.enterprise.jms;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    /**
     * 保理系统导入资产时, 创建企业
     */
    @Value("${alt.rabbitmq.queue.contractEnterpriseCreate}")
    private String contractEnterpriseCreate;
    /**
     * 保理系统导入资产时, 创建企业 回调
     */
    @Value("${alt.rabbitmq.queue.contractEnterpriseCreateCallback}")
    private String contractEnterpriseCreateCallback;
    /**
     * 企业系统更新企业时, 同步企业信息到 保理系统
     */
    @Value("${alt.rabbitmq.queue.contractEnterpriseUpdateEnterprise}")
    private String contractEnterpriseUpdateEnterprise;

    @Bean
    public Queue contractEnterpriseCreateQueue() {
        return new Queue(this.contractEnterpriseCreate, true, false, false);
    }

    @Bean
    public Queue contractEnterpriseCreateCallbackQueue() {
        return new Queue(this.contractEnterpriseCreateCallback, true, false, false);
    }

    @Bean
    public Queue contractEnterpriseUpdateEnterprise() {
        return new Queue(this.contractEnterpriseUpdateEnterprise, true, false, false);
    }
}
