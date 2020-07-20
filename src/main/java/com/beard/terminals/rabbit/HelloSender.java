package com.beard.terminals.rabbit;

import com.beard.terminals.domain.User;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by angry_beard on 2020/7/20.
 */
@Component
public class HelloSender {

    private final AmqpTemplate rabbitTemplate;

    public HelloSender(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send() {
        String context = "hello " + new Date();
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("hello", context);
    }

    public void send(User user) {
        System.out.println("Sender object: " + user.toString());
        this.rabbitTemplate.convertAndSend("object", user);
    }
}
