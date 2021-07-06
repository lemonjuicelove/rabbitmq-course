package com.whut.rabbitmq.eight;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 死信 队列
 * 消费者2
 */
public class Consumer2 {

    // 死性队列的名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        DeliverCallback deliverCallback = (consumerTag,message) -> {
            System.out.println("Consumer2接收的消息：" + new String(message.getBody()));
        };

        CancelCallback cancelCallback = consumerTag -> {

        };

        channel.basicConsume(DEAD_QUEUE,true,deliverCallback,cancelCallback);
    }

}
