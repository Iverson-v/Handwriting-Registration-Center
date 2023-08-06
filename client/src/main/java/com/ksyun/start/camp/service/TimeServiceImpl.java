package com.ksyun.start.camp.service;

import com.ksyun.start.camp.entity.Constant;
import com.ksyun.start.camp.entity.RegistryVo;
import com.ksyun.start.camp.entity.TimeVo;
import com.ksyun.start.camp.service.TimeService;
import com.ksyun.start.camp.utils.HttpClientUtils;
import com.ksyun.start.camp.utils.jaksonutils.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * 代表远端时间服务接口实现
 */
@Component
public class TimeServiceImpl implements TimeService {

    // 开始编写你的逻辑，下面是提示
    // 1. 调用 TimeService 获取远端服务返回的时间
    // 2. 获取到自身的 serviceId 信息
    // 3. 组合相关信息返回

    @Autowired
    private ClientServiceImpl clientService;

    @Override
    public String getDateTime(String style) {
        // 开始编写你的逻辑，下面是提示
        // 1. 连接到 registry 服务，获取服务列表
        // 2. 从远端服务列表中获取一个服务实例
        // 3. 执行远程调用，获取指定格式的时间


        // 1. 连接到 registry 服务，获取一个服务实例
        RegistryVo registryVo = clientService.discovery();
        if (registryVo==null){//如果注册中心没找到服务直接返回null
            return null;
        }

        //拼接服务的ip+port，，/api/getDateTime?style=full
        String url="http://"+registryVo.getIpAddress()+":"+registryVo.getPort();
        url+="/api/getDateTime?style="+style;
        String jsonStr="";
        try {
            // 2. 执行远程调用
            jsonStr= HttpClientUtils.get(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }




        return jsonStr;
    }



}
