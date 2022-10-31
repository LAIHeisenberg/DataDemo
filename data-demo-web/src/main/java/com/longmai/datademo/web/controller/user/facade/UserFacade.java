package com.longmai.datademo.web.controller.user.facade;

import com.longmai.datademo.dao.po.UserPo;
import com.longmai.datademo.dto.UserDto;
import com.longmai.datademo.security.service.dto.JwtUserDto;
import com.longmai.datademo.service.UserService;
import com.longmai.datademo.utils.PageUtil;
import com.longmai.datademo.utils.SecurityUtils;
import com.longmai.datademo.web.controller.user.param.UserQueryParam;
import com.longmai.datademo.web.controller.user.view.UserView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserFacade {

    @Autowired
    private UserService userService;

    public Object listAllUser(UserQueryParam userQueryParam){
        List<UserDto> userDtos = userService.listAllEnabledUser(userQueryParam);
        userDtos.stream().map(new Function<UserDto, UserView>() {
            @Override
            public UserView apply(UserDto userDto) {
                UserView view = new UserView();
                BeanUtils.copyProperties(userDto, view);
//                view.setCreateTimeDesc(DateUtil.localDateTimeFormat(DateUtil.fromTimeStamp(userDto.getCreateTime()),"yyyy-MM-dd HH:mm:ss"));
                view.setAuthMethod(userDto.getAuthMethod() == 1 ? "UKEY":"用户口令");
                return view;
            }
        }).collect(Collectors.toList());
        JwtUserDto currentUser = (JwtUserDto) SecurityUtils.getCurrentUser();
        Map<String, Object> map = PageUtil.toPage(userDtos, userDtos.size());
        map.put("currentUser", currentUser.getUsername());
        map.put("phone", currentUser.getUser().getPhone());
        return map;
    }

    public boolean save(UserDto userDto){
        UserPo userPo = new UserPo();
        BeanUtils.copyProperties(userDto,userPo);
        userPo.setEnabled(true);
        return userService.save(userPo);
    }

}
