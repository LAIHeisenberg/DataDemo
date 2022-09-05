package com.longmai.datademo.service.impl;

import com.longmai.datademo.dao.mapper.UserMapper;
import com.longmai.datademo.dao.po.UserPo;
import com.longmai.datademo.dto.UserDto;
import com.longmai.datademo.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public void createUser(UserDto userDto){
        UserPo userPo = new UserPo();
        BeanUtils.copyProperties(userDto, userPo);
        userPo.setCreateTime(Instant.now().getEpochSecond());

        userMapper.insert(userPo);
    }

}
