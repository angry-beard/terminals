package com.beard.terminals.controller;

import com.beard.terminals.domain.SysUser;
import com.beard.terminals.pipeline.PipelineTask;
import com.beard.terminals.pipeline.PipelineTaskFactory;
import com.beard.terminals.pipeline.utils.JsonUtils;
import com.beard.terminals.process.OriginProcess;
import com.beard.terminals.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by angry_beard on 2020/7/13.
 */
@Slf4j
@Api(tags = "管道操作api")
@RestController
@RequestMapping("pipeline")
public class PipelineController {

    private final OriginProcess originProcess;
    private final PipelineTaskFactory pipelineTaskFactory;

    public PipelineController(PipelineTaskFactory pipelineTaskFactory, OriginProcess originProcess) {
        this.originProcess = originProcess;
        this.pipelineTaskFactory = pipelineTaskFactory;
    }

    @PostMapping("init")
    @ApiOperation(value = "初始化管道", notes = "业务对象")
    @ApiImplicitParam(paramType = "sysUser")
    public Result init(SysUser sysUser) {
        PipelineTask task = pipelineTaskFactory.newPipelineTask("testSerial_001", "TEST_PIPELINE", originProcess, sysUser);
        log.debug("初始化管道成功 result:{}", JsonUtils.toJSONString(task));
        return Result.date(task.getExecStatus());
    }

    @PostMapping("reload")
    @ApiOperation(value = "启动操作", notes = "业务对象")
    @ApiImplicitParam(paramType = "map")
    public Result reload(Map map) {
        PipelineTask task = pipelineTaskFactory.restoreTaskFromDatabase("testSerial_001");
        task.run();
        log.debug("重启化管道成功 result:{}", JsonUtils.toJSONString(task));
        return Result.date(task.getExecStatus());
    }
}
