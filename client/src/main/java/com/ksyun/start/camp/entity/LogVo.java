package com.ksyun.start.camp.entity;


import lombok.Data;

@Data
public class LogVo {

    private String serviceName;
    private String serviceId;
    private String datetime;
    private String level;
    private String message;

    public LogVo(String serviceName, String serviceId, String datetime, String level, String message) {
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.datetime = datetime;
        this.level = level;
        this.message = message;
    }
}
