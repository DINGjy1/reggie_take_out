package org.example.reggie.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/*
自定义业务异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
