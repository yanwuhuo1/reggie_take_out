package com.it.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//自定义元数据对象处理器
@Slf4j
@Component
public class MyMetaObjecthandler implements MetaObjectHandler {
    //插入操作，自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("insertFill" + metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
//        metaObject.setValue("createUser", BaseContext.getCurrentId());
//        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    //修改操作，自动填充
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("updateFill" + metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
//        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        metaObject.setValue("updateTime",LocalDateTime.now());

    }

}
