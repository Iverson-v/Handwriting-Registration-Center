package com.ksyun.start.camp.entity;



import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@Data
public class RegistryVo {
    private String serviceName;
    private String serviceId;
    private String ipAddress;
    private Integer port;
    private long registerTime;

    public RegistryVo(String serviceName, String serviceId, String ipAddress, Integer port) {
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistryVo that = (RegistryVo) o;
        return serviceId.equals(that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId);
    }
}
