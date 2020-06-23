package com.beard.terminals.service.impl;

import com.beard.terminals.domain.User;
import com.beard.terminals.service.IUserService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author angry_beard
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Override
    public List<User> list() {
        return Lists.newArrayList(User.builder()
                .name("哪吒")
                .age(16)
                .sex("男")
                .build());
    }

    @Override
    public User queryById(Integer id) {
        return User.builder()
                .name("大圣")
                .age(22)
                .sex("男")
                .build();
    }

    @Override
    public Boolean addUser(User user) {
        return true;
    }

    @Override
    public Boolean delById(Integer id) {
        return true;
    }

    @Override
    public Boolean updateUser(User user) {
        return true;
    }
}
