package com.whut.rabbitmq.two;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 一个工作线程
 */
public class Work01 {

    // 队列名称
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {

        // 获取信道
        Channel channel = RabbitMQUtils.getChannel();

        // 消息接收时的回调函数
        DeliverCallback deliverCallback = (var1,var2) -> {
            System.out.println(new String(var2.getBody()));
        };

        // 消息取消时的回调函数
        CancelCallback cancelCallback = var -> {
            System.out.println(var + "消息被取消");
        };
        System.out.println("C2等到接收消息...");

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
