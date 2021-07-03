package com.whut.rabbitmq.seven;

import com.rabbitmq.client.Channel;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 */
public class EmitLogTopic {

    // 交换机的名称
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();

        Map<String,String> bindingKey = new HashMap<>();

        bindingKey.put("quick.orange.fox","被队列 Q1 接收到");
        bindingKey.put("lazy.brown.fox","被队列 Q2 接收到");
        bindingKey.put("lazy.pink.rabbit","虽然满足两个绑定但只被队列 Q2 接收一次");
        bindingKey.put("quick.brown.fox","不匹配任何绑定不会被任何队列接收到会被丢弃");
        bindingKey.put("quick.orange.male.rabbit","是四个单词不匹配任何绑定会被丢弃");
        bindingKey.put("lazy.orange.male.rabbit","是四个单词但匹配 Q2");

        for (Map.Entry<String, String> entry : bindingKey.entrySet()) {
            String routingKey = entry.getKey();
            String message = entry.getValue();
            channel.basicPublish(EXCHANGE_NAME,routingKey,null,message.getBytes());
            System.out.println("生产者发送消息：" + message);
        }


    }

}
