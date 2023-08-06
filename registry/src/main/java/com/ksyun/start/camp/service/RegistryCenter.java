package com.ksyun.start.camp.service;

import com.ksyun.start.camp.entity.RegistryVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RegistryCenter {
    public Map<String, Set<RegistryVo>> getRegisterMap();
    public boolean registryService(RegistryVo registryVo);
    public boolean unRegistryService(RegistryVo registryVo);

    public List<RegistryVo> discoveryService();
    List<RegistryVo> discoveryServiceByName(String serviceName);
    boolean heartbeat(RegistryVo registryVo);
}
