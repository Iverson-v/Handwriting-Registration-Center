package com.ksyun.start.camp.service;

/**
 * 代表一个时间服务接口
 */
public interface TimeService {

    /**
     * 从远端服务获取当前时间
     *
     * @param style 时间格式
     * @return 指定格式的时间字符串
     */
    String getDateTime(String style);
}