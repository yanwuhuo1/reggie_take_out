package com.it.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.it.dto.OrdersDto;
import com.it.entity.Orders;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author CodeJiao
 * @since 2022-05-13
 */
public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);

    OrdersDto getW(Orders orders);
}
