package com.it.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.dto.OrdersDto;
import com.it.entity.OrderDetail;
import com.it.entity.Orders;
import com.it.mapper.OrderDetailMapper;
import com.it.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author CodeJiao
 * @since 2022-05-13
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {


}
