package com.beard.terminals.process;

import com.beard.terminals.domain.SysUser;
import com.beard.terminals.pipeline.AbstractPipelineProcess;
import com.beard.terminals.pipeline.ProcessParameter;
import com.beard.terminals.pipeline.ProcessResult;
import com.beard.terminals.pipeline.utils.JsonUtils;
import com.beard.terminals.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by angry_beard on 2020/7/13.
 */
@Slf4j
@Component
public class OriginProcess extends AbstractPipelineProcess {

    private final PostProcess postProcess;
    private ISysUserService sysUserService;

    public OriginProcess(PostProcess postProcess, ISysUserService sysUserService) {
        this.postProcess = postProcess;
        this.sysUserService = sysUserService;
    }

    @Override
    public ProcessResult process(ProcessParameter parameterMap) {
        SysUser sysUser = (SysUser) parameterMap.getRequestParam();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!isPass(sysUser)) {
            return paused();
        }
        List<SysUser> list = sysUserService.findAll();
        log.info("处理业务逻辑 list:{}", JsonUtils.toJSONString(list));
        return nextProcess(postProcess);
    }

    private boolean isPass(SysUser sysUser) {
        log.info("request params: {}", JsonUtils.toJSONString(sysUser));
        return true;
    }
}
