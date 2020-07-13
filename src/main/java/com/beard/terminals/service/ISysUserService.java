package com.beard.terminals.service;

import com.beard.terminals.domain.SysUser;

import java.util.List;

public interface ISysUserService {

    boolean insert(SysUser sysUser);

    List<SysUser> findAll();
}
