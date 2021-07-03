package com.whut.rabbitmq.seven;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 声明主题交换机及相关队列
 */
public class ReceiveLogsTopic1 {

    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,"topic");
        channel.queueDeclare("Q1",false,false,false,null);
        channel.queueBind("Q1",EXCHANGE_NAME,"*.orange.*");

        // 接收消息
        DeliverCallback deliverCallback = (var1, var2) -> {
            System.out.println("ReceiveLogsTopic1：" + new String(var2.getBody()));
            System.out.println("接收队列Q1，绑定键：" + var2.getEnvelope().getRoutingKey());
        };
        CancelCallback cancelCallback = var -> {

        };

        channel.basicConsume("Q1",true,deliverCallback,cancelCallback);
    }

}
