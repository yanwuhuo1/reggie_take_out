package com.it.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 地址管理 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2022-05-15
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}
