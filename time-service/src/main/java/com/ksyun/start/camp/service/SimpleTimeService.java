package com.ksyun.start.camp.service;

/**
 * 代表简单时间服务接口
 */
public interface SimpleTimeService {

    /**
     * 根据指定格式获取当前时间
     *
     * @param style 格式
     * @return 指定格式的时间字符串
     */
    String getDateTime(String style);
}
