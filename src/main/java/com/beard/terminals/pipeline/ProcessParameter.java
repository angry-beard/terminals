package com.beard.terminals.pipeline;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by angry_beard on 2020/7/13.
 * <p>
 * 用于流水线处理传递参数的类
 * </p>
 */
@Getter
@Builder
public class ProcessParameter {
    private PipelineTask pipelineTask;

    /* 流水线的执行参数 */
    private Object requestParam;

    /* 流水线当前执行的 process 的次序 */
    private Integer order;

    /* 流水线当前执行的 process 在数据库中的 id */
    private Integer id;

    /* 上一个 process 传递过来的 resultParam */
    private Object resultParam;

}
