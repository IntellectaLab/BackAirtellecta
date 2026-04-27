package com.airtellecta.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    public boolean success;
    public T data;
    public String error;

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.data = data;
        return r;
    }

    public static <T> ApiResponse<T> fail(String error) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = false;
        r.error = error;
        return r;
    }
}
