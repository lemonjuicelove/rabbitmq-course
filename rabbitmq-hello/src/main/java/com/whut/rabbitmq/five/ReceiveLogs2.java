package com.whut.rabbitmq.five;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息接收
 */
public class ReceiveLogs2 {

    // 交换机的名称
    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();
        // 声明一个交换机
        /**
         * 参数1：交换机的名称
         * 参数2：交换机的类型
         */
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        // 声明一个临时队列
        /**
         * 临时队列的名称是随机的
         * 当消费者断开与队列的连接时，队列就自动删除
         */
        String queueName = channel.queueDeclare().getQueue();

        /**
         * 绑定交换机与队列
         * 参数1：队列名称
         * 参数2：交换机名称
         * 参数3：routingKey的值
         */
        channel.queueBind(queueName,EXCHANGE_NAME,"");

        DeliverCallback deliverCallback = (var1,var2) -> {
            System.out.println("ReceiveLogs2接收到的消息：" + new String(var2.getBody()));
        };
        CancelCallback cancelCallback = var -> {

        };
        channel.basicConsume(queueName,true,deliverCallback,cancelCallback);

    }

}
