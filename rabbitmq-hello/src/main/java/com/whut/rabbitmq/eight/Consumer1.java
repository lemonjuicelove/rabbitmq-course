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
 * 消费者1
 */
public class Consumer1 {

    // 普通交换机的名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    // 死信交换机的名称
    public static final String DEAD_EXCHANGE = "dead_exchange";
    // 普通队列的名称
    public static final String NORMAL_QUEUE = "normal_queue";
    // 死性队列的名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        // 声明死信和普通交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        // 声明普通队列
        Map<String, Object> arguments = new HashMap<>();
        // 正常队列设置死信交换机
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        // 设置死信交换机的routingkey
        arguments.put("x-dead-letter-routing-key","lisi");
        // 设置正常队列长度的限制
        // arguments.put("x-max-length",6);
        // 设置过期时间
        // arguments.put("x-message-ttl",10*1000);
        channel.queueDeclare(NORMAL_QUEUE,false,false,false,arguments);
        // 声明死信队列
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);

        // 绑定交换机与队列
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");


        DeliverCallback deliverCallback = (consumerTag,message) -> {
            String msg = new String(message.getBody());
            if ("info5".equals(msg)){
                System.out.println("Consumer1拒绝的消息：" + msg);
                /**
                 * 参数1：消息的标签
                 * 参数2：是否重新放回队列，true：放回    false：不放回
                 */
                channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
            }else {
                System.out.println("Consumer1接收的消息：" + msg);
                /**
                 * 参数1：消息的标签
                 * 参数2：是否批量应答，true：批量   false：不批量
                 */
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
            }
        };

        CancelCallback cancelCallback = consumerTag -> {

        };

        // 开启手动应答
        channel.basicConsume(NORMAL_QUEUE,false,deliverCallback,cancelCallback);
    }

}
