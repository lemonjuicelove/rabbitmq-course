package com.whut.rabbitmq.consumer;

import com.whut.rabbitmq.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 备份队列消费者
 */
@Slf4j
@Component
public class BackupConsumer {

    @RabbitListener(queues = ConfirmConfig.BACKUP_QUEUE_NAME)
    public void receiverBackupMsg(Message message){
        String msg = new String(message.getBody());
        log.info("备份发现不可路由消息：{}",msg);
    }
}
