package com.whut.rabbitmq.eight;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 死信队列  生产者
 */
public class Producer {

    // 普通交换机的名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        // 死信消息 设置ttl时间(过期时间)
        // AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();

        for (int i = 0; i < 10; i++) {
            String message = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE,"zhangsan",null,message.getBytes());
        }

    }
}
