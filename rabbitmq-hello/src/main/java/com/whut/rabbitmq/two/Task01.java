package com.whut.rabbitmq.two;

import com.rabbitmq.client.Channel;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

// 生产者，发送大量的消息
public class Task01 {

    // 队列名称
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();

        // 声明一个队列
        /**
         * 生成一个队列
         * 参数1：队列名称
         * 参数2：队列里面的消息是否持久化，默认消息存储在内存当中
         * 参数3：该队列是否只供一个消费者进行消费，是否进行消息共享，true允许多个消费者消费，false只能一个消费者消费
         * 参数4：是否自动删除，最后一个消费者断开连接以后，该队列是否自动删除，true自动删除，false不自动删除
         * 参数5：其他参数
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        // 从控制台接收信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();

            /**
             * 发送一个消息
             * 参数1：发送到哪个交换机
             * 参数2：路由的key值是哪个，本次是队列的名称
             * 参数3：其他参数信息
             * 参数4：发送消息的消息体
             */
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());

            System.out.println("发送消息完成：" + message);
        }

    }

}
