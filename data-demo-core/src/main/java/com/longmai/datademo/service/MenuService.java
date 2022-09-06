package com.longmai.datademo.service;

import com.longmai.datademo.dto.MenuDto;
import com.longmai.datademo.vo.MenuVo;

import java.util.List;

public interface MenuService {


    List<MenuDto> findByUser(Long currentUserId);

    List<MenuDto> buildTree(List<MenuDto> menuDtos);

    List<MenuVo> buildMenus(List<MenuDto> menuDtos);
}
