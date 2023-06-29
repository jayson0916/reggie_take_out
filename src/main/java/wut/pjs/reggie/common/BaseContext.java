package wut.pjs.reggie.common;


/**
 * @Author: Jayson_P
 * @Date: 2023/06/08/11:07
 */

/*
* 基于ThreadLocal封装工具类，用于保存当前登录用户的Id
* */
public class BaseContext{
    public static ThreadLocal<Long> ThreadLocal = new ThreadLocal<Long>();

    //设置值
    public static void setCurrentId(Long id){
        ThreadLocal.set(id);
    }

    //获取值
    public static Long getCurrentId(){
        return ThreadLocal.get();
    }
}
