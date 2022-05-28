package com.it.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2022-05-14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
