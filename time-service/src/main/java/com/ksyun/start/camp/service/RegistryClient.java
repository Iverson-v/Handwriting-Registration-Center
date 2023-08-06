package com.ksyun.start.camp.service;


import com.ksyun.start.camp.entity.Constant;
import com.ksyun.start.camp.entity.RegistryVo;
import com.ksyun.start.camp.utils.HttpClientUtils;
import com.ksyun.start.camp.utils.jaksonutils.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


@Service
public class RegistryClient {

    @Autowired
    private Environment environment;

    //生成UUID，作为服务ID
    final String serviceId = UUID.randomUUID().toString();
    public String getServiceId() {
        return serviceId;
    }


    //向注册中心进行注册
    public void registerToRedistryCenter() {
        //1.获得当前服务名
        String serviceName=environment.getProperty("spring.application.name");
        //2.获得当前服务端口号
        Integer port=Integer.parseInt(environment.getProperty("server.port"));
        //3.获得当前服务ip
        String ipAddress="";
        try {
            ipAddress= InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        //4.封装VO类作为请求体
        RegistryVo registryVo = new RegistryVo(serviceName, serviceId, ipAddress, port);
        //5.转换成json字符串
        String registryVoJsonStr = JacksonUtil.toJsonStr(registryVo);
        //6.获得注册中心地址。
        String rgistryCenterUrl=environment.getProperty("registration-center-location");
        //7.拼接总路径
        rgistryCenterUrl=rgistryCenterUrl+ Constant.REGISTER_SUFFIX;
        //8.发送httpclient请求。
        try {
            HttpClientUtils.postAndSetBody(rgistryCenterUrl, registryVoJsonStr, "application/json",
                    "UTF-8", 30000, 30000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void sendHeartbeat() {
        //1.获得当前服务名
        String serviceName=environment.getProperty("spring.application.name");
        //2.获得当前服务端口号
        Integer port=Integer.parseInt(environment.getProperty("server.port"));
        //3.获得当前服务ip
        String ipAddress="";
        try {
            ipAddress= InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        //4.封装VO类作为请求体
        RegistryVo registryVo = new RegistryVo(serviceName, serviceId, ipAddress, port);
        //5.转换成json字符串
        String registryVoJsonStr = JacksonUtil.toJsonStr(registryVo);
        //6.获得注册中心地址。
        String rgistryCenterUrl=environment.getProperty("registration-center-location");
        //7.拼接总路径
        rgistryCenterUrl=rgistryCenterUrl+ Constant.HEARTBEAT;

        //8.开启定时任务
        Timer timer=new Timer();
        String finalRgistryCenterUrl = rgistryCenterUrl;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    //9.发送httpclient请求。
                    HttpClientUtils.postAndSetBody(finalRgistryCenterUrl, registryVoJsonStr, "application/json",
                            "UTF-8", 30000, 30000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        },5000,3000);
    }

    public void unRegistry() {
        //1.获得当前服务名
        String serviceName=environment.getProperty("spring.application.name");
        //2.获得当前服务端口号
        Integer port=Integer.parseInt(environment.getProperty("server.port"));
        //3.获得当前服务ip
        String ipAddress="";
        try {
            ipAddress= InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        //4.封装VO类作为请求体
        RegistryVo registryVo = new RegistryVo(serviceName, serviceId, ipAddress, port);
        //5.转换成json字符串
        String registryVoJsonStr = JacksonUtil.toJsonStr(registryVo);
        //6.获得注册中心地址。
        String rgistryCenterUrl=environment.getProperty("registration-center-location");
        //7.拼接总路径
        rgistryCenterUrl=rgistryCenterUrl+ Constant.UNREGISTER_SUFFIX;
        //8.发送httpclient请求。
        try {
            HttpClientUtils.postAndSetBody(rgistryCenterUrl, registryVoJsonStr, "application/json",
                    "UTF-8", 30000, 30000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
