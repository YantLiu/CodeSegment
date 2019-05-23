package com.alt.enterprise.jms;

import com.alt.base.common.dto.CustomerInfoToEnterpriseDTO;
import com.alt.base.common.dto.response.BaseRespDTO;
import com.alt.base.common.enums.BusinessType;
import com.alt.enterprise.biz.ContractEnterpriseCreateBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author liuyanting
 * @description RabbitMQ 消息接收器
 * @date: 2019/03/22
 */
@Component
@Slf4j
public class RabbitConsumer {
    @Autowired
    private ContractEnterpriseCreateBiz contractEnterpriseCreateBiz;
    @Autowired
    private RabbitProducer rabbitProducer;

    /**
     * @param reqDTO
     * @description 保理系统导入资产时, 创建企业
     * @author liuyanting
     * @date 2019/3/22
     */
    @RabbitHandler
    @RabbitListener(queues = "${alt.rabbitmq.queue.contractEnterpriseCreate}")
    public void contractEnterpriseCreate(CustomerInfoToEnterpriseDTO reqDTO) {
        log.info("RabbitReceiver.contractEnterpriseCreate>>{}", reqDTO);
        BaseRespDTO resp = contractEnterpriseCreateBiz.contractEnterpriseCreate(reqDTO);
        //导入保理申请时, 不回调
        if (reqDTO.getBusinessType().equals(BusinessType.FACTORING_APPLY_IMPORT)){
            return;
        }
        if (resp.isSuccess()) {
            reqDTO.setSuccess(true);
            rabbitProducer.contractEnterpriseCreateCallback(reqDTO);
        } else {
            reqDTO.setSuccess(false);
            reqDTO.setMessage(resp.getMessage());
            rabbitProducer.contractEnterpriseCreateCallback(reqDTO);
        }
    }
}
