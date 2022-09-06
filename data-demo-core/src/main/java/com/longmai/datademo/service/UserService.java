package com.longmai.datademo.service;

import com.longmai.datademo.dto.UserDto;
import com.longmai.datademo.dto.UserLoginDto;

import java.util.List;

public interface UserService {

    List<UserDto> listAllEnabledUser();

    UserDto findByDn(String dn);

    UserLoginDto getUserLoginDto(String userName);

}
