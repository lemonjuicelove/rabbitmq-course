package com.whut.rabbitmq.three;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 消息在手动应答时不丢失，放回队列中重新消费
 */
public class Task2 {

    public static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取信道
        Channel channel = RabbitMQUtils.getChannel();

        // 声明队列
        // 队列持久化
        channel.queueDeclare(TASK_QUEUE_NAME,true,false,false,null);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            // 发送消息
            // 消息持久化
            channel.basicPublish("",TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            System.out.println("生产者发出消息");
        }

    }

}
