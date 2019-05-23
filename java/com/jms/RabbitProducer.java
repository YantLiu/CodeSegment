package com.alt.enterprise.jms;

import com.alt.base.common.dto.CustomerInfoToEnterpriseDTO;
import com.alt.base.common.dto.EnterpriseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author liuyanting
 * @description RabbitMQ 消息生产者
 * @date: 2019/03/22
 */
@Component
@Slf4j
public class RabbitProducer {
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

    @Autowired
    public RabbitTemplate rabbitTemplate;

    public void sendMsg(String queueName, final Object object) {
        rabbitTemplate.convertAndSend(queueName, object);
    }

    /**
     * @description 保理系统导入资产时, 创建企业 回调
     * @param dto
     * @author liuyanting
     * @date 2019/3/22
     */
    public void contractEnterpriseCreateCallback(CustomerInfoToEnterpriseDTO dto) {
        log.info("RabbitReceiver.contractEnterpriseCreateCallback>>{}", dto);
        this.sendMsg(contractEnterpriseCreateCallback, dto);
    }

    /**
     * @description 保理系统导入资产时, 创建企业 回调
     * @param dto
     * @author liuyanting
     * @date 2019/3/22
     */
    public void contractEnterpriseUpdateEnterprise(EnterpriseDTO dto) {
        log.info("RabbitReceiver.contractEnterpriseUpdateEnterprise>>{}", dto);
        this.sendMsg(contractEnterpriseUpdateEnterprise, dto);
    }
}
