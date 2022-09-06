package com.longmai.datademo.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longmai.datademo.dao.po.MenuPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<MenuPo> {

    List<MenuPo> selectMenuByRoleId(@Param("roleIds") Collection<Long> roleId);

}
