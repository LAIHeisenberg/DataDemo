package com.longmai.datademo.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longmai.datademo.dao.po.RolePo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<RolePo> {

    List<Long> listRoleIdByUserId(@Param("userId")Long userId);

    List<RolePo> selectBatchRoleIds(@Param("roleIds") Collection<Long> roleIds);
}
