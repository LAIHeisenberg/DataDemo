package com.longmai.datademo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.longmai.datademo.dao.mapper.UserMapper;
import com.longmai.datademo.dao.po.UserPo;
import com.longmai.datademo.dto.UserDto;
import com.longmai.datademo.dto.UserLoginDto;
import com.longmai.datademo.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Override
    public List<UserDto> listAllEnabledUser(){
        QueryWrapper<UserPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", 1);
        return userMapper.selectList(queryWrapper).stream().map(new Function<UserPo, UserDto>() {
            @Override
            public UserDto apply(UserPo userPo) {
                UserDto userDto = new UserDto();
                BeanUtils.copyProperties(userPo, userDto);
                return userDto;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public UserDto findByDn(String dn){
        return null;
    }

    @Override
    public UserLoginDto getUserLoginDto(String userName){
        QueryWrapper queryWrapper = new QueryWrapper(UserPo.class);
        UserPo queryParam = new UserPo();
        queryParam.setUserName(userName);
        queryWrapper.setEntity(queryParam);
        UserPo userPo = userMapper.selectOne(queryWrapper);
        UserLoginDto userLoginDto = null;
        if (Objects.nonNull(userPo)){
            userLoginDto = new UserLoginDto();
            BeanUtils.copyProperties(userPo, userLoginDto);
        }
        return userLoginDto;
    }


    @Override
    public boolean save(UserPo userPo) {
        userMapper.insert(userPo);
        return false;
    }
}
