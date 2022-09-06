package com.longmai.datademo.service;

import com.longmai.datademo.dto.AuthorityDto;
import com.longmai.datademo.dto.UserDto;
import com.longmai.datademo.dto.UserLoginDto;

import java.util.List;

public interface RoleService {

    List<Long> listRoleIds(Long userId);

    List<AuthorityDto> mapToGrantedAuthorities(UserLoginDto user);

}
