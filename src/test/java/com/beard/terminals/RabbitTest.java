package com.beard.terminals;

import com.beard.terminals.domain.User;
import com.beard.terminals.rabbit.ApiCreditSender;
import com.beard.terminals.rabbit.FanoutSender;
import com.beard.terminals.rabbit.HelloSender;
import com.beard.terminals.rabbit.TopicSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by angry_beard on 2020/7/20.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitTest {

    @Autowired
    private HelloSender helloSender;


    // 发送单条消息
    @Test
    public void contextLoads() {
        helloSender.send();
    }

    // 发送对象
    @Test
    public void sendObj() {
        User user = new User();
        user.setAge(1);
        user.setName("admin");
        user.setSex("1234");
        helloSender.send(user);
    }

    @Autowired
    private TopicSender topicSender;

    @Test
    public void send1() {
        topicSender.send1();
    }

    @Test
    public void send2() {
        topicSender.send2();
    }

    @Autowired
    private ApiCreditSender sender;

    @Test
    public void test_creditBank_type() {
        Map<String, Object> head = new HashMap<>();
        head.put("type", "cash");
        sender.creditBank(head, "银行授信(部分匹配)");
    }

    @Test
    public void test_creditBank_all() {
        Map<String, Object> head = new HashMap<>();
        head.put("type", "cash");
        head.put("aging", "fast");
        sender.creditBank(head, "银行授信(全部匹配)");
    }

    @Test
    public void test_creditFinance_type() {
        Map<String, Object> head = new HashMap<>();
        head.put("type", "cash");
        sender.creditFinance(head, "金融公司授信(部分匹配)");
    }

    @Test
    public void test_creditFinance_all() {
        Map<String, Object> head = new HashMap<>();
        head.put("type", "cash");
        head.put("aging", "fast");
        sender.creditFinance(head, "金融公司授信(全部匹配)");
    }

    @Autowired
    private FanoutSender fanoutSender;

    @Test
    public void sendFanout() {
        fanoutSender.send();
    }
}
