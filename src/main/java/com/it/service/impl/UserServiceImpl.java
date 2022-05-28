package com.it.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.entity.User;
import com.it.mapper.UserMapper;
import com.it.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2022-05-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
