package com.mightcell.exception;

/**
 * @author: MightCell
 * @description: 自定义业务异常
 * @date: Created in 22:29 2023-02-25
 */
public class CodeException extends RuntimeException{
    public CodeException(String message) {super(message);}
}
