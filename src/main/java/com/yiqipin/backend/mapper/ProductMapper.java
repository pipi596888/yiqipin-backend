package com.yiqipin.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiqipin.backend.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
