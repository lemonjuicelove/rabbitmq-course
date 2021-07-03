package com.whut.rabbitmq.three;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息在手动应答时不丢失，放回队列中重新消费
 */
public class Work4 {

    public static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取信道
        Channel channel = RabbitMQUtils.getChannel();

        System.out.println("C2处理消息时间较长");

        DeliverCallback deliverCallback = (var1,var2) -> {
            try {
                Thread.sleep(20*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("接收到的消息：" + new String(var2.getBody()));

            // 手动应答的方法
            /**
             * 参数1：消息的标记 tag
             * 参数2：是否批量应答，false：不批量应答，true：批量应答
             */
            channel.basicAck(var2.getEnvelope().getDeliveryTag(),false);
        };

        CancelCallback cancelCallback = var -> {
            System.out.println("消费者取消消费");
        };

        // 设置不公平分发
        // int prefetchCount = 1;
        // 设置预取值为5
        int prefetchCount = 5;
        /**
         * 参数为0：表示轮训分发
         * 参数为1：不公平分发
         * 参数为其他值：设置预取值
         */
        channel.basicQos(prefetchCount);

        // 采用手动应答
        channel.basicConsume(TASK_QUEUE_NAME,false,deliverCallback,cancelCallback);
    }

}
