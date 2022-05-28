package com.it.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理
@Slf4j
@RestControllerAdvice(annotations = {RestController.class, Controller.class})//拦截带RestController和Controller注解的类
public class GlobalExceptionHandler {

    //异常处理方法
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    //    ExceptionHandler拦截指定的异常
    public R<String> exceptHandler(SQLIntegrityConstraintViolationException ex) {
        log.info(ex.getMessage());
        if (ex.getMessage().contains("Duplicate")) {
            String[] s = ex.getMessage().split(" ");
            String msg = s[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    //异常处理方法
    @ExceptionHandler(CustomException.class)
    //    ExceptionHandler拦截指定的异常
    public R<String> exceptHandler(CustomException ex) {
        log.info(ex.getMessage());

        return R.error(ex.getMessage());
    }

}
