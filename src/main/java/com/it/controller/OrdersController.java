package com.it.controller;


import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.common.BaseContext;
import com.it.common.R;
import com.it.dto.OrdersDto;
import com.it.entity.AddressBook;
import com.it.entity.Category;
import com.it.entity.OrderDetail;
import com.it.entity.Orders;
import com.it.service.AddressBookService;
import com.it.service.OrderDetailService;
import com.it.service.OrdersService;
import lombok.extern.slf4j.Slf4j;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author CodeJiao
 * @since 2022-05-13
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/page")
    public R<Page> save(int page, int pageSize, String number, String beginTime, String endTime) throws Exception{
        Page<Orders> objectPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

       /* LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(number != null, Orders::getId, number);*/

        QueryWrapper<Orders> wrapper = new QueryWrapper<>();

        wrapper.like("number", StringUtils.isNotBlank(number)?number:"");
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date end = formatter.parse(endTime);
            Date begin = formatter.parse(beginTime);
            wrapper.between("order_time",begin,end);
        }

        wrapper.orderByDesc("checkout_time");
        //添加排序条件，根据更新时间降序排列
        //lambdaQueryWrapper.orderByDesc(Orders::getCheckoutTime);
        ordersService.page(objectPage, wrapper);
        List<Orders> records = objectPage.getRecords();
        List<OrdersDto> collect = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            //对象拷贝
            BeanUtils.copyProperties(item, ordersDto);
            Long id = item.getAddressBookId();
            AddressBook byId = addressBookService.getById(id);
            if (byId != null) {
                String number1 = byId.getConsignee();
                ordersDto.setUserName(number1);
            }
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(collect);
        return R.success(ordersDtoPage);

    }

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("orders={}", orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> ding(int page, int pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);

        Page<OrdersDto> ordersDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Orders::getCheckoutTime);
        ordersService.page(ordersPage, lambdaQueryWrapper);

        List<Orders> records = ordersPage.getRecords();

        //设置订单明细
        List<OrdersDto> collect = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            //对象拷贝
            BeanUtils.copyProperties(item, ordersDto);

            //查询订单明细
            Long id = item.getId();
            QueryWrapper<OrderDetail> wrapper = new QueryWrapper<>();
            wrapper.eq("order_id", id);
            List<OrderDetail> list = orderDetailService.list(wrapper);

            //赋值给中间对象
            ordersDto.setOrderDetails(list);

            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(collect) ;

        return R.success(ordersDtoPage);
    }

    //更改状态
    @PutMapping
    public R<String> updatestatus(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return R.success("修改状态成功");
    }

    //再来一单
    @PostMapping("/again")
    public R<OrdersDto> Again(@RequestBody Orders orders) {
        log.info("ordersDto,{}", orders);
        System.out.println("id = " + orders);
        OrdersDto w = ordersService.getW(orders);
        log.info("www:{}",w);

        return R.success(w);
    }

}
