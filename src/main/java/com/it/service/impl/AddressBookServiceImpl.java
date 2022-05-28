package com.it.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.entity.AddressBook;
import com.it.mapper.AddressBookMapper;
import com.it.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 地址管理 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2022-05-15
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
