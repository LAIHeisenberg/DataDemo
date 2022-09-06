package com.longmai.datademo.web.controller.user.facade;

import com.longmai.datademo.dto.UserDto;
import com.longmai.datademo.service.UserService;
import com.longmai.datademo.utils.DateUtil;
import com.longmai.datademo.web.controller.user.view.UserView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserFacade {

    @Autowired
    private UserService userService;

    public List<UserView> listAllUser(){
        List<UserDto> userDtos = userService.listAllEnabledUser();
        return userDtos.stream().map(new Function<UserDto, UserView>() {
            @Override
            public UserView apply(UserDto userDto) {

                UserView view = new UserView();
                BeanUtils.copyProperties(userDto, view);
//                view.setCreateTimeDesc(DateUtil.localDateTimeFormat(DateUtil.fromTimeStamp(userDto.getCreateTime()),"yyyy-MM-dd HH:mm:ss"));
                view.setAuthMethod(userDto.getAuthMethod() == 1 ? "UKEY":"用户口令");
                return view;
            }
        }).collect(Collectors.toList());
    }


}
