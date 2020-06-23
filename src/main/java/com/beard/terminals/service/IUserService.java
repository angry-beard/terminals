package com.beard.terminals.service;

import com.beard.terminals.domain.User;

import java.util.List;

/**
 * @author angry_beard
 */
public interface IUserService {

    List<User> list();

    User queryById(Integer id);

    Boolean addUser(User user);

    Boolean delById(Integer id);

    Boolean updateUser(User user);
}
