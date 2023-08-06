package com.ksyun.start.camp.entity;

import lombok.Data;


@Data
public class ResponseTimeVo {
    private int code;
    private String msg;
    private TimeVo data;
}
