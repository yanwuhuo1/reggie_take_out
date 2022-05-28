package com.it.dto;


import com.it.entity.OrderDetail;
import com.it.entity.Orders;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
