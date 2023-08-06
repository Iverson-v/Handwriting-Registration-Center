package com.ksyun.start.camp.service;

import com.ksyun.start.camp.entity.*;
import com.ksyun.start.camp.utils.HttpClientUtils;
import com.ksyun.start.camp.utils.jaksonutils.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 客户端服务实现
 */
@Component
public class ClientServiceImpl implements ClientService {

    @Autowired
    private Environment environment;
    final String serviceId = UUID.randomUUID().toString();

    public String getServiceId() {
        return serviceId;
    }

    public void registerToRedistryCenter() {

        String serviceName=environment.getProperty("spring.application.name");
        Integer port=Integer.parseInt(environment.getProperty("server.port"));
        String ipAddress="";
        try {
            ipAddress= InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        RegistryVo registryVo = new RegistryVo(serviceName, serviceId, ipAddress, port);
        String registryVoJsonStr = JacksonUtil.toJsonStr(registryVo);
        String rgistryCenterUrl=environment.getProperty("registration-center-location");
        rgistryCenterUrl=rgistryCenterUrl+ Constant.REGISTER_SUFFIX;


        try {
            HttpClientUtils.postAndSetBody(rgistryCenterUrl, registryVoJsonStr, "application/json",
                    "UTF-8", 30000, 30000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    //发送心跳，每隔60s发送。
    public void sendHeartbeat() {
        //1.获得服务名
        String serviceName=environment.getProperty("spring.application.name");
        //2.获得端口号
        Integer port=Integer.parseInt(environment.getProperty("server.port"));
        //3.获得ip地址
        String ipAddress="";
        try {
            ipAddress= InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        //4.封装出参对象，作为请求体
        RegistryVo registryVo = new RegistryVo(serviceName, serviceId, ipAddress, port);
        String registryVoJsonStr = JacksonUtil.toJsonStr(registryVo);

        //5.获得注册中心地址。
        String rgistryCenterUrl=environment.getProperty("registration-center-location");
        rgistryCenterUrl=rgistryCenterUrl+ Constant.HEARTBEAT;

        //6.开启定时任务，每隔60s进行一次心跳连接。延迟10秒开始定时。
        Timer timer=new Timer();
        String finalRgistryCenterUrl = rgistryCenterUrl;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    HttpClientUtils.postAndSetBody(finalRgistryCenterUrl, registryVoJsonStr, "application/json",
                            "UTF-8", 30000, 30000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        },10000,60000);
    }

    public void unRegistry() {
        String serviceName=environment.getProperty("spring.application.name");
        Integer port=Integer.parseInt(environment.getProperty("server.port"));
        String ipAddress="";

        try {
            ipAddress= InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        RegistryVo registryVo = new RegistryVo(serviceName, serviceId, ipAddress, port);
        String registryVoJsonStr = JacksonUtil.toJsonStr(registryVo);
        String rgistryCenterUrl=environment.getProperty("registration-center-location");


        rgistryCenterUrl=rgistryCenterUrl+ Constant.UNREGISTER_SUFFIX;
        try {
            HttpClientUtils.postAndSetBody(rgistryCenterUrl, registryVoJsonStr, "application/json",
                    "UTF-8", 30000, 30000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //从time-service服务中选择一个返回。
    public RegistryVo discovery(){
        //1.获得注册中心discovery接口
        String rgistryCenterUrl=environment.getProperty("registration-center-location");
        //GET /api/discovery?name=time-service // 带负载均衡的、指定名称的应用发现
        rgistryCenterUrl=rgistryCenterUrl+ Constant.DISCOVERY+"?name=time-service";

        String jsonStr="";
        try {
            jsonStr = HttpClientUtils.get(rgistryCenterUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //2.该接口返回一个列表，其中有一个地址，可以访问time-service服务
        ResponseDiscoveryVo responseDiscoveryVo = JacksonUtil.toBean(jsonStr, ResponseDiscoveryVo.class);
        List<RegistryVo> registryVos = responseDiscoveryVo.getData();

        //如果对应的服务为空。也就是对应服务不可用的时候
        if (registryVos==null){
            System.err.println("client服务调用服务发现出错，time-service服务未注册！");
            return null;
        }
        RegistryVo registryVo = registryVos.get(0);
        return registryVo;
    }


    //向日志服务器发送数据
    public void logging() {
        //获得服务名
        String serviceName=environment.getProperty("spring.application.name");

        //开启定时任务
        Timer timer=new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                //获得当前时间
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                String formattedNow = now.format(formatter);

                //封装VO类
                LogVo logVo = new LogVo(serviceName,serviceId,formattedNow,"info","Client status is OK!");
                //转换为字符串
                String logVoStr = JacksonUtil.toJsonStr(logVo);
                //拼接发送请求的地址
                String rgistryCenterUrl=environment.getProperty("logging-service-location");
                rgistryCenterUrl=rgistryCenterUrl+ Constant.LOGGING;
                try {
                    HttpClientUtils.postAndSetBody(rgistryCenterUrl, logVoStr, "application/json",
                            "UTF-8", 30000, 30000);
                } catch (Exception e) {
                    Calendar calendar= Calendar.getInstance();
                    SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = dateFormat.format(calendar.getTime());
                    System.err.println("HttpClient发送到日志服务出错，日志服务无响应。"+date);
                    //throw new RuntimeException(e);
                }
            }
        },0,1000);
    }
}
