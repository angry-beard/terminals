package com.beard.terminals;

import com.beard.terminals.pipeline.utils.JsonUtils;
import com.beard.terminals.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TerminalsApplicationTests {

    @Autowired
    private ISysUserService sysUserService;

    @Test
    public void test() {
        log.info("detail:{}", JsonUtils.toJSONString(sysUserService.findAll()));
    }
}
