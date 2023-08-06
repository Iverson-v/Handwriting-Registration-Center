package com.ksyun.start.camp.service;

import com.ksyun.start.camp.entity.LogVo;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 日志服务的实现
 */
@Component
public class LoggingServiceImpl implements LoggingService {


    private Map<String,List<LogVo>> map = new ConcurrentHashMap<>();
    private AtomicInteger logIdCounter = new AtomicInteger(1);

    @Override
    public boolean logging(LogVo logVo) {
        //根据服务id查询对应的list
        List<LogVo> logVos = map.get(logVo.getServiceId());
        if (logVos==null){
            //首次添加当前服务名的日志。应创建一个list储存。
            List<LogVo> logs=new ArrayList<>();
            logVo.setLogId(logIdCounter.getAndIncrement());
            logs.add(logVo);
            map.put(logVo.getServiceId(),logs);
            return true;
        }else{
            // 先检查日志是否已存在
            if (!logVos.contains(logVo)) {
                logVo.setLogId(logIdCounter.getAndIncrement());
                logVos.add(logVo);
                return true;
            }else {
                return false;
            }
        }


    }

    @Override
    public List<LogVo> getLogsByServiceId(String serviceId) {
        //根据服务id查询对应的list
        List<LogVo> logVos = map.get(serviceId);

        // 根据 logVos 中的 logId 进行倒序排序
        if (logVos != null && !logVos.isEmpty()) {
            logVos = logVos.stream()
                    .sorted(Comparator.comparing(LogVo::getLogId).reversed())
                    .limit(5)  // 仅取前5条记录
                    .collect(Collectors.toList());
        }
        return logVos;
    }

    @Override
    public List<LogVo> getAllLogs() {
        List<LogVo> logVos=new ArrayList<>();
        Set<String> keySet = map.keySet();
        for (String key: keySet) {
            List<LogVo> logVosItem = map.get(key);
            logVos.addAll(logVosItem);
        }

        // 根据 logVos 中的 logId 进行倒序排序
        if (!logVos.isEmpty()) {
            logVos = logVos.stream()
                    .sorted(Comparator.comparing(LogVo::getLogId).reversed())
                    .collect(Collectors.toList());
        }

        return logVos;
    }
}
