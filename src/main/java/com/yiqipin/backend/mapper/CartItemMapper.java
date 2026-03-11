package com.yiqipin.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiqipin.backend.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
