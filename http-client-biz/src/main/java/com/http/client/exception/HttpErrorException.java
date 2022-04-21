package com.http.client.exception;

/**
 * 异常类
 * @author linzhou
 */
public class HttpErrorException extends RuntimeException{

    public HttpErrorException(String message) {
        super(message);
    }
}
