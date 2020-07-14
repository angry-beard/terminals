package com.beard.terminals.process;

import com.beard.terminals.domain.SysUser;
import com.beard.terminals.pipeline.AbstractPipelineProcess;
import com.beard.terminals.pipeline.ProcessParameter;
import com.beard.terminals.pipeline.ProcessResult;
import com.beard.terminals.pipeline.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by angry_beard on 2020/7/13.
 */
@Slf4j
@Component
public class PostProcess extends AbstractPipelineProcess {

    @Override
    public ProcessResult process(ProcessParameter parameterMap) {
        List<SysUser> list = (List<SysUser>) parameterMap.getResultParam();
        log.info("list:{}", JsonUtils.toJSONString(list));
        return terminalProcess();
    }
}
