package com.beard.terminals.controller;

import com.beard.terminals.dao.SysUserMapper;
import com.beard.terminals.domain.SysUser;
import com.beard.terminals.domain.User;
import com.beard.terminals.service.ISysUserService;
import com.beard.terminals.service.IUserService;
import com.beard.terminals.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author angry_beard
 */
@Api(tags = "用户操作API")
@RestController
@Slf4j
@RequestMapping("user")
public class UserController {

    private final IUserService userService;
    private final ISysUserService sysUserService;

    public UserController(IUserService userService, ISysUserService sysUserService) {
        this.userService = userService;
        this.sysUserService = sysUserService;
    }

    @GetMapping("list")
    @ApiOperation(value = "获取所有用户列表", notes = "无需传参")
    @ApiImplicitParam(paramType = "query")
    public Result<List<User>> list() {
        log.info(userService.list().toString());
        //return new Result(userService.list());
        return new Result(sysUserService.findAll());
    }

    @GetMapping("detail")
    @ApiOperation(value = "查询用户具体信息", notes = "通过user.id查询用信息")
    @ApiImplicitParam(name = "id", value = "用户ID", paramType = "query", required = true, dataType = "Integer")
    public Result<User> detail(@RequestParam("id") Integer id) {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>id " + id);
        return new Result(userService.queryById(id));
    }

    @PutMapping("add")
    @ApiOperation(value = "新增用户信息", notes = "单条新增用户信息")
    @ApiImplicitParam(name = "User", value = "用户实体", paramType = "add", required = true, dataType = "com.beard.terminals.domain.User")
    public Result add(User user) {
        userService.addUser(user);
        SysUser sysUser = new SysUser();
        sysUser.setAccount("admin");
        sysUser.setAvatar("http://106.14.119.243/upload/2020/1/1F415104624-10-71736464073d422191d96b7869d9d27b.jpg");
        sysUser.setName("admin");
        sysUser.setSex("F");
        sysUser.setPassword("admin");
        sysUser.setBirthday(new Date());
        sysUser.setPhone("2323");
        sysUser.setEmail("admin@163.com");
        sysUserService.insert(sysUser);
        return Result.success();
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除用户信息", notes = "根据id删除用户信息")
    @ApiImplicitParam(name = "id", value = "用户id", paramType = "delete", required = true, dataType = "Integer")
    public Result del(@RequestParam("id") Integer id) {
        userService.delById(id);
        return Result.success();
    }

    @PostMapping("update")
    @ApiOperation(value = "修改用户信息", notes = "修改用户信息")
    @ApiImplicitParam(name = "User", value = "用户实体", paramType = "update", required = true, dataType = "com.beard.terminals.domain.User")
    public Result update(User user) {
        userService.updateUser(user);
        return Result.success();
    }
}
