package com.it.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author CodeJiao
 * @since 2022-05-13
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}
