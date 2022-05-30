package com.it.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//基于ThreadLocal封装工具类，用户保存和获取当前登入用户id
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置id
     *
     * @param id
     */
    public static void setCurrentId(Long id) {
        log.info("BaseContextUtil function setCurrentId({}) run .... ", id);
        threadLocal.set(id);
    }

    /**
     * 获取id
     *
     * @return
     */
    public static Long getCurrentId() {
        log.info("BaseContextUtil function getCurrentId({}) run .... ", threadLocal.get());
        return threadLocal.get();
    }
}
