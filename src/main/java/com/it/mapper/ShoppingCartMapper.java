package com.it.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 购物车 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2022-05-15
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

}
