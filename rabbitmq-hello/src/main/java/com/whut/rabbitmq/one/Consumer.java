package com.whut.rabbitmq.one;

import com.rabbitmq.client.*;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// 消费者：消费消息
public class Consumer {
    // 队列名称
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();

        // 声明接收消息的回调
        DeliverCallback deliverCallback = (var1,var2) -> {
            System.out.println(new String(var2.getBody()));
        };
        // 声明取消消息时的回调
        CancelCallback cancelCallback = (var) -> {
            System.out.println("消息消费被中断");
        };

        /**
         * 消费者消费消息
         * 参数1：消费哪个队列
         * 参数2：消费成功之后是否要自动应答，true代表自动应答，false代表手动应答
         * 参数3：消费者成功消费的回调函数
         * 参数4：消费者取消消费的回调函数
         */
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);

    }
}
