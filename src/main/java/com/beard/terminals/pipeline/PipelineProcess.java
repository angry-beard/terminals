package com.beard.terminals.pipeline;

/**
 /**
 * Created by angry_beard on 2020/7/13.
 * <p>
 * 流水线执行子任务，实现的时候，需要注意保持该子类的所有 process 方法是无状态
 * </p>
 */
public interface PipelineProcess {

    /**
     * 子任务处理
     *
     * @param parameterMap 参数 map
     * @return 一个处理结果，类型为 ProcessResult，
     * @see ProcessResult
     */
    ProcessResult process(ProcessParameter parameterMap);

    /**
     * 是否是终止点
     *
     * @return 如果这个方法返回 true，然后流水线会停止执行，并且当前 PipelineProcess 的 process 方法不会被调用
     * 如果这个方法返回 false，流水线框架会继续调用 nextProcess() 寻找下一个要处理的 PipelineProcess
     */
    boolean isTerminal();

    /**
     * 获取当前跑的 process 的名称
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

}
