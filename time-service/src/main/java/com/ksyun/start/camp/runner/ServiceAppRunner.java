package com.ksyun.start.camp.runner;

import com.ksyun.start.camp.service.RegistryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 服务启动运行逻辑
 */
@Component
public class ServiceAppRunner implements ApplicationRunner {

    @Autowired
    private RegistryClient registryClient;
    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 此处代码会在 Boot 应用启动时执行

        // 开始编写你的逻辑，下面是提示
        // 1. 向 registry 服务注册当前服务
        registryClient.registerToRedistryCenter();



        // 2. 定期发送心跳逻辑
        registryClient.sendHeartbeat();


        // TODO
    }
    //3.程序结束注销注册中心。
    @PreDestroy
    private void unRegistry(){
        registryClient.unRegistry();
    }
}
