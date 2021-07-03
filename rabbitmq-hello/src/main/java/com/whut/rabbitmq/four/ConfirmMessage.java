package com.whut.rabbitmq.four;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.whut.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

/**
 * 发布确认模式
 *  1、单个确认      耗时：489ms
 *  2、批量确认      耗时：64ms
 *  3、异步批量确认  耗时：25ms
 */
public class ConfirmMessage {

    // 批量发消息的数量
    public static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws InterruptedException, TimeoutException, IOException {
        // publishMessageIndividually();
        // publishMessageBatch();
        publishMessageAsync();
    }

    // 单个确认
    public static void publishMessageIndividually() throws IOException, TimeoutException, InterruptedException {

        Channel channel = RabbitMQUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        // 开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();

        String message = "";
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            message = i + "";
            channel.basicPublish("",queueName,null,message.getBytes());
            // 进行发布确认
            boolean flag = channel.waitForConfirms();

        }

        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end-begin) + "ms");
    }

    // 批量发布确认
    public static void publishMessageBatch() throws IOException, TimeoutException, InterruptedException {

        Channel channel = RabbitMQUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        // 开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();

        // 批量确认消息的大小
        int batchSize = 100;

        String message = "";
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            message = i + "";
            channel.basicPublish("",queueName,null,message.getBytes());
            if (i%batchSize == 0){
                // 发布确认
                channel.confirmSelect();
            }
        }

        // 避免有消息没有确认
        channel.confirmSelect();

        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end-begin) + "ms");
    }

    // 异步发布确认
    public static void publishMessageAsync() throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        // 开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();

        /**
         * 准备一个线程安全有序的哈希表，适用于高并发情况下
         * 1.将序号与消息进行关联
         * 2.根据序号进行删除
         * 3.支持多线程
         */
        ConcurrentSkipListMap<Long,String> outstandingConfirms = new ConcurrentSkipListMap<>();

        // 消息确认成功的回调函数
        /**
         * 参数1：消息的标记
         * 参数2：是否未批量确认
         */
        ConfirmCallback ackCallback = (var1, var3) -> {
            // 2：删除已经确认的消息
            if (var3){
                // 返回一个小于等于当前序列号的未确认消息
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(var1,true);
                confirmed.clear();
            }else {
                outstandingConfirms.remove(var1);
            }
            System.out.println("确认的消息：" + var1);
        };
        // 消息确认失败的回调函数
        ConfirmCallback nackCallback = (var1, var3) -> {
            // 3：打印未确认的消息
            String message2 = outstandingConfirms.get(var1);

            System.out.println("未确认的消息：" + message2 + ",未确认的消息的标记：" + var1);
        };

        // 准备消息的监听器，监听哪些消息成功，哪些消息失败
        /**
         * 参数1：监听哪些消息成功
         * 参数2：监听哪些消息失败
         */
        channel.addConfirmListener(ackCallback,nackCallback);

        String message = "";
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            message = i + "";
            channel.basicPublish("",queueName,null,message.getBytes());

            // 1：记录所有要发送的消息
            outstandingConfirms.put(channel.getNextPublishSeqNo(),message);
        }

        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end-begin) + "ms");
    }


}
