package com.ksyun.start.camp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代表此 API 的返回对象
 */
@Data
@NoArgsConstructor
public class ApiResponse {

    /**
     * 代表此 API 的响应返回码
     * 200 表示成功，非 200 表示失败
     */
    private String error;

    private String data;

    public ApiResponse(String error, String data) {
        this.error = error;
        this.data = data;
    }
}
