package com.beard.terminals.pipeline;

import org.springframework.stereotype.Component;

/**
 * Created by angry_beard on 2020/7/13.
 */
@Component
public class TerminalProcess implements PipelineProcess {

    @Override
    public ProcessResult process(ProcessParameter parameterMap) {
        return ProcessResult.builder()
                .processExecStatus(PipelineExecStatus.DONE)
                .build();
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public String getName() {
        return "TerminalProcess";
    }
}
