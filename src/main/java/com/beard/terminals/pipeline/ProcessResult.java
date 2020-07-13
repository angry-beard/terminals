package com.beard.terminals.pipeline;

import lombok.Builder;
import lombok.Data;

/**
 * Created by angry_beard on 2020/7/13.
 */
@Data
@Builder
public class ProcessResult {

    private PipelineProcess nextProcess;

    private PipelineExecStatus processExecStatus;

    private Object resultParam;
}
