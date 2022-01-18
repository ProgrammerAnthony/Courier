package com.courier.core.exception;

/**
 * @author Anthony
 * @create 2022/1/19
 * @desc
 */
public class CourierException extends RuntimeException{
    public CourierException() {
    }

    public CourierException(Exception e) {
        super(e);
    }

    public CourierException(String message) {
        super(message);
    }
}
