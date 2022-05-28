package com.it.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.entity.ShoppingCart;
import com.it.mapper.ShoppingCartMapper;
import com.it.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2022-05-15
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
