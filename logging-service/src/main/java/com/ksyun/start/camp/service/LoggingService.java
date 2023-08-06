package com.ksyun.start.camp.service;

import com.ksyun.start.camp.entity.LogVo;

import java.util.List;

/**
 * 日志服务实现接口
 */
public interface LoggingService {
    boolean logging(LogVo logVo);

    List<LogVo> getLogsByServiceId(String serviceId);

    List<LogVo> getAllLogs();

    // TODO: 实现日志服务接口
    // 此处不再重复提示骨架代码，可参考其他 Service 接口的定义

}
