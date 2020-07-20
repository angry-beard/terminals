package com.beard.terminals.rabbit;

import com.beard.terminals.domain.User;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by angry_beard on 2020/7/20.
 */
@Component
@RabbitListener(queues = "hello")
public class HelloReceiver {

    @RabbitHandler
    public void process(String hello) {
        System.out.println("Receiver  : " + hello);
    }

    @RabbitHandler
    public void process(User user) {
        System.out.println("Receiver object : " + user);
        System.out.println("name:" + user.getName());
        System.out.println("sex:" + user.getSex());
    }

    @Component
    @RabbitListener(queues = "topic.message")
    public class TopicReceiver {
        @RabbitHandler
        public void process(String message) {
            System.out.println("Topic Receiver1  : " + message);
        }
    }
}
