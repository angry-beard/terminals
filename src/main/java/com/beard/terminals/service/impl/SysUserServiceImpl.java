package com.beard.terminals.service.impl;

import com.beard.terminals.mapper.SysUserMapper;
import com.beard.terminals.domain.SysUser;
import com.beard.terminals.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public boolean insert(SysUser sysUser) {
        sysUserMapper.insertSelective(sysUser);
        return true;
    }

    @Override
    public List<SysUser> findAll() {
        return sysUserMapper.selectByExample(null);
    }

}
