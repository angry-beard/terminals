package com.beard.terminals.controller;

import com.beard.terminals.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @Author he.huang
 * @create 2020/6/29 15:44
 */
@Slf4j
@Api(tags = "文件操作api")
@RestController
@RequestMapping("file")
public class FileController {

    @PostMapping("upload")
    @ApiOperation(value = "上传单个文件", notes = "file")
    @ApiImplicitParam(paramType = "file")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail();
        }
        String fileName = file.getOriginalFilename();
        String path;
        try {
            path = ResourceUtils.getURL("classpath:").getPath();
            File dest = new File(path +"//tempFile//"+ fileName);
            file.transferTo(dest);
            return Result.success();
        } catch (IOException e) {
            log.error("upload error detail: ", e);
            return Result.fail();
        }
    }

    @PostMapping("batch-upload")
    @ApiOperation(value = "批量上传文件", notes = "无需传参")
    @ApiImplicitParam(paramType = "query")
    public Result batchUpload() {
        return Result.success();
    }

    @PostMapping("download")
    @ApiOperation(value = "下载文件", notes = "无需传参")
    @ApiImplicitParam(paramType = "query")
    public Result download() {
        return Result.success();
    }
}
