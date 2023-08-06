package com.ksyun.start.camp.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResponseDiscoveryVo {
    private int code;
    private String msg;
    private List<RegistryVo> data;
}
