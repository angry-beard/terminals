package com.beard.terminals.process;

import com.beard.terminals.pipeline.AbstractPipelineProcess;
import com.beard.terminals.pipeline.ProcessParameter;
import com.beard.terminals.pipeline.ProcessResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by angry_beard on 2020/7/13.
 */
@Slf4j
@Component
public class PostProcess extends AbstractPipelineProcess {

    @Override
    public ProcessResult process(ProcessParameter parameterMap) {
        log.info("结束");
        return terminalProcess();
    }
}
