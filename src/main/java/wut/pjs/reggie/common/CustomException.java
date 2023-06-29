package wut.pjs.reggie.common;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/10/10:48
 */

//自定义异常类
public class CustomException extends RuntimeException{
    public CustomException(String msg){
        super(msg);
    }
}
