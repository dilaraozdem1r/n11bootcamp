package com.n11bootcamp.rabbitmq.consumer;

import com.n11bootcamp.rabbitmq.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @RabbitListener(queues = "n11bootcamp_notification")
    public void consumeJsonMessage(User user){
        LOGGER.info(String.format("Received JSON message -> %s", user.firstName+" " +user.lastName));
    }
}