package com.whut.rabbitmq.six;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsDirect2 {

    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        // 声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"direct");
        // 声明一个队列
        channel.queueDeclare("disk",false,false,false,null);
        // 绑定交换机与队列
        channel.queueBind("disk",EXCHANGE_NAME,"error");

        DeliverCallback deliverCallback = (var1, var2) -> {
            System.out.println("ReceiveLogsDirect2接收到的消息：" + new String(var2.getBody()));
        };
        CancelCallback cancelCallback = var -> {

        };

        channel.basicConsume("disk",true,deliverCallback,cancelCallback);

    }

}
