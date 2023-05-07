package org.example.reggie.common;
/*
基于ThreadLocal封装工具类，保存/获取当前用户的id
 */
public class BaseContext {
    private static  ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setThreadLocal(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
