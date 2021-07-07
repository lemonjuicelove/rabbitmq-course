package com.whut.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 确认回调接口 RabbitTemplate.ConfirmCallback
 * 消息退回接口 RabbitTemplate.ReturnsCallback
 */
@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {

    @Resource
    private RabbitTemplate rabbitTemplate;


    @PostConstruct
    public void init(){
        // 将该实现类注入到RabbitTemplate.ConfirmCallback当中
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 交换机确认回调方法
     *  1.交换机接收到了消息，会回调
     *      1.1 correlationData 保存回调消息的id及相关信息
     *      1.2 ack=true 交换机收到了消息
     *      1.3 cause=null
     *  2.交换机没有收到消息，会回调
     *      1.1 correlationData 保存回调消息的id及相关信息
     *      1.2 ack=false 交换机没有收到消息
     *      1.3 cause 失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (ack){
            log.info("交换机收到id为：{}的消息",id);
        }else {
            log.info("交换机没有收到id1为：{}的消息，原因是：{}",id,cause);
        }
    }

    // 可以在消息传递过程中不可达目的地时，将消息返回给生产者
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.error("消息：{}，被交换机{}退回，退回原因：{}，路由key：{}",
                new String(returnedMessage.getMessage().getBody()),
                returnedMessage.getExchange(),returnedMessage.getReplyText(),returnedMessage.getRoutingKey());
    }

}
