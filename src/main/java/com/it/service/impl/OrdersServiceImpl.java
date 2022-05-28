package com.it.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.common.BaseContext;
import com.it.common.CustomException;
import com.it.dto.OrdersDto;
import com.it.entity.*;
import com.it.mapper.OrdersMapper;
import com.it.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author CodeJiao
 * @since 2022-05-13
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    AddressBookService addrBookService;

    @Autowired
    OrderDetailService orderDetailService;

    @Override
    public void submit(Orders orders) {
        //获取当前用户id
        Long currentId = BaseContext.getCurrentId();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        if (list == null || list.size() == 0) {
            throw new CustomException("购物车为空不能下单");
        }
        //查询用户信息
        User user = userService.getById(currentId);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addrBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("地址信息有误，不能下单");
        }
        long id = IdWorker.getId();//订单号
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> collect = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;

        }).collect(Collectors.toList());

        //向订单表插入数据，一条数据
        orders.setId(id);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(id);
        orders.setNumber(String.valueOf(id));
//        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail())
        );
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(collect);
        //清空购物车数据
        shoppingCartService.remove(lambdaQueryWrapper);
    }

    @Override
    public OrdersDto getW(Orders orders) {
        Orders byId = this.getById(orders);

        OrdersDto ordersDto = new OrdersDto();

        //查询当前菜品对应的口味信息，从dish_flavor表查询
//        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
//
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderDetail::getOrderId,byId.getId());
        List<OrderDetail> list = orderDetailService.list(lambdaQueryWrapper);
        ordersDto.setOrderDetails(list);

        BeanUtils.copyProperties(byId,ordersDto);

        return ordersDto;
    }


}
