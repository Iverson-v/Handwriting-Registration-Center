package com.ksyun.start.camp.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class LogVo {

    private int logId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogVo logVo = (LogVo) o;
        return serviceName.equals(logVo.serviceName) && serviceId.equals(logVo.serviceId) && datetime.equals(logVo.datetime) && level.equals(logVo.level) && message.equals(logVo.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, serviceId, datetime, level, message);
    }
}
