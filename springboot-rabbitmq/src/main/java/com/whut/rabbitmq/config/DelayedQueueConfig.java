package com.whut.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayedQueueConfig {

    // 交换机
    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    // 队列
    public static final String DELAYED_QUEUE_NAME = "delayed.queue";
    // routingKey
    public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";

    // 声明队列
    @Bean
    public Queue delayedQueue(){
        return new Queue(DELAYED_QUEUE_NAME);
    }

    // 声明交换机：自定义的交换机
    @Bean
    public CustomExchange delayedExchange(){

        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type","direct"); // 延迟类型

        /**
         * 自定义交换机的参数
         * 参数1：交换机的名称
         * 参数2：交换机的类型
         * 参数3：是否需要持久化
         * 参数4：是否需要自动删除
         * 参数5：其他的参数
         */
        return new CustomExchange(DELAYED_EXCHANGE_NAME,"x-delayed-message",true,false,arguments);
    }

    // 绑定
    @Bean
    public Binding delayedQueueBindingDelayedExchange(@Qualifier("delayedQueue") Queue delayedQueue,
                                                      @Qualifier("delayedExchange") CustomExchange delayedExchange){
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAYED_ROUTING_KEY).noargs();
    }



}
