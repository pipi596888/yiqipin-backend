package com.yiqipin.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiqipin.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
