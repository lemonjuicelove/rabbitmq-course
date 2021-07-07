package com.whut.rabbitmq.web;

import com.whut.rabbitmq.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 发送延迟消息
 */
@Slf4j // 日志注解
@RestController
@RequestMapping("/ttl")
public class SendMsgController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable String message){
        log.info("当前时间：{}，发送消息给两个TTL队列：{}",new Date().toString(),message);
        rabbitTemplate.convertAndSend("X","XA","消息来自ttl为10s的队列:" + message);
        rabbitTemplate.convertAndSend("X","XB","消息来自ttl为40s的队列:" + message);
    }

    @RequestMapping("/sendTtlMsg/{message}/{ttlTime}")
    public void sendMsg(@PathVariable String message,@PathVariable String ttlTime){
        log.info("当前时间：{},发送时长为{}毫秒的TTL信息给队列QC：{}",new Date().toString(),ttlTime,message);

        // 发送延时消息
        rabbitTemplate.convertAndSend("X","XC",message,message1 -> {
            message1.getMessageProperties().setExpiration(ttlTime);
            return message1;
        });
    }

    // 基于插件的发送延迟消息
    @RequestMapping("/sendDelayMsg/{message}/{delayTime}")
    public void sendMsg(@PathVariable String message,@PathVariable Integer delayTime){
        log.info("当前时间：{},发送时长为{}毫秒的信息给延迟队列delayed.queue：{}",new Date().toString(),delayTime,message);

        // 发送延时消息
        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANGE_NAME,DelayedQueueConfig.DELAYED_ROUTING_KEY,message,message1 -> {
            message1.getMessageProperties().setDelay(delayTime);
            return message1;
        });
    }


}
