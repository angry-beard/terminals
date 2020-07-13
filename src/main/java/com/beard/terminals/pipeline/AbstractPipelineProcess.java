package com.beard.terminals.pipeline;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by angry_beard on 2020/7/13.
 */
public abstract class AbstractPipelineProcess implements PipelineProcess {

    @Autowired
    protected TerminalProcess terminalProcess;


    @Override
    public boolean isTerminal() {
        return false;
    }

    protected ProcessResult terminalProcess() {
        return ProcessResult.builder()
                .nextProcess(terminalProcess)
                .processExecStatus(PipelineExecStatus.DONE)
                .build();
    }

    protected ProcessResult nextProcess(PipelineProcess pipelineProcess) {
        return ProcessResult.builder()
                .nextProcess(pipelineProcess)
                .processExecStatus(PipelineExecStatus.DONE)
                .build();
    }

    protected ProcessResult nextProcess(PipelineProcess pipelineProcess, Object resultParam) {
        return ProcessResult.builder()
                .nextProcess(pipelineProcess)
                .resultParam(resultParam)
                .processExecStatus(PipelineExecStatus.DONE)
                .build();
    }

    protected ProcessResult paused() {
        return ProcessResult.builder()
                .processExecStatus(PipelineExecStatus.PAUSED)
                .build();
    }
}
