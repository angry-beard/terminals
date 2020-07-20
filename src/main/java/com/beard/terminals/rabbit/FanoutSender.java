package com.beard.terminals.rabbit;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by angry_beard on 2020/7/20.
 */
@Component
public class FanoutSender {


    private final AmqpTemplate rabbitTemplate;

    public FanoutSender(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send() {
        String context = "hi, fanout msg ";
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("fanoutExchange", "", context);
    }
}
