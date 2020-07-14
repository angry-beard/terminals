package com.beard.terminals.controller;

import com.beard.terminals.constant.PipelineTaskName;
import com.beard.terminals.domain.SysUser;
import com.beard.terminals.pipeline.PipelineExecStatus;
import com.beard.terminals.pipeline.PipelineTaskFactory;
import com.beard.terminals.pipeline.domain.PipelineTask;
import com.beard.terminals.pipeline.domain.PipelineTaskExample;
import com.beard.terminals.pipeline.mapper.PipelineTaskMapper;
import com.beard.terminals.pipeline.utils.JsonUtils;
import com.beard.terminals.process.OriginProcess;
import com.beard.terminals.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
    private final PipelineTaskMapper pipelineTaskMapper;

    public PipelineController(PipelineTaskFactory pipelineTaskFactory, OriginProcess originProcess, PipelineTaskMapper pipelineTaskMapper) {
        this.originProcess = originProcess;
        this.pipelineTaskFactory = pipelineTaskFactory;
        this.pipelineTaskMapper = pipelineTaskMapper;
    }

    @PostMapping("init")
    @ApiOperation(value = "初始化管道", notes = "业务对象")
    @ApiImplicitParam(paramType = "sysUser")
    public Result init(SysUser sysUser) {
        pipelineTaskFactory.newPipelineTask("testSerial_001", PipelineTaskName.TEST_PIPELINE.toString(), originProcess, sysUser);
        log.debug("初始化管道成功 request:{}", JsonUtils.toJSONString(sysUser));
        return Result.date(PipelineExecStatus.INIT);
    }

    @PostMapping("query-result")
    @ApiOperation(value = "启动操作", notes = "pipeline任务名称")
    @ApiImplicitParam(paramType = "taskName")
    public Result reload(@RequestParam("taskName") String taskName) {
        com.beard.terminals.pipeline.domain.PipelineTask task = getPipelineTask(taskName);
        if (Objects.isNull(task)) {
            return Result.fail();
        }
        if (PipelineExecStatus.INIT.toString().equals(task.getExecStatus())
                || PipelineExecStatus.PAUSED.toString().equals(task.getExecStatus())) {
            pipelineTaskFactory.restoreTaskFromDatabase(taskName).run();
            return Result.date(task.getExecStatus());
        }
        log.debug("查看执行结果 result:{}", JsonUtils.toJSONString(task));
        return Result.date(task.getExecStatus());
    }

    private PipelineTask getPipelineTask(String taskName) {
        PipelineTaskExample example = new PipelineTaskExample();
        example.createCriteria().andNameEqualTo(taskName);
        example.setOrderByClause("id limit 1");
        List<PipelineTask> list = pipelineTaskMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }
}
