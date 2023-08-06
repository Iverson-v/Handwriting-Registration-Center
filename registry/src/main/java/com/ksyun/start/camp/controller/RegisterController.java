package com.ksyun.start.camp.controller;


import com.ksyun.start.camp.entity.RegistryVo;
import com.ksyun.start.camp.entity.RestResult;
import com.ksyun.start.camp.service.RegistryCenter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RequestMapping("/api")
@Api(tags="注册中心")
@RestController
public class RegisterController {


    @Autowired
    private RegistryCenter registryCenter;


    @ApiOperation("向注册中心注册")
    @PostMapping ("/register")
    public RestResult register(@RequestBody RegistryVo registryVo){
        if(registryVo==null||registryVo.getServiceId()==null||registryVo.getServiceName()==null||registryVo.getIpAddress()==null||registryVo.getPort()==null){
            return RestResult.failure().msg("入参错误，注册失败！");
        }



        //1.把当前时间存在对象中，表示第一次注册的时间。
        long registerTime = System.currentTimeMillis();
        registryVo.setRegisterTime(registerTime);
        //2.注册中心进行注册
        boolean flag = registryCenter.registryService(registryVo);
        if(!flag){
            return RestResult.failure().msg("该服务已经在注册中心存在！");
        }

        //3.开启定时任务，延迟5s后，每隔3s就执行一次定时任务，如果当前服务，超过60s还没发送心跳，就注销。
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long timeMillis = System.currentTimeMillis();


                Map<String, Set<RegistryVo>> registerMap = registryCenter.getRegisterMap();
                Set<RegistryVo> registryVos = registerMap.get(registryVo.getServiceName());

                //如果没找到，表示这个服务已经被注销了，就关闭定时任务。等待其重写注册再开启定时任务。
                if(!registryVos.contains(registryVo)){
                    //如果没找到，表示这个服务已经被注销了，就关闭定时任务。等待其重写注册再开启定时任务。
                    cancel();
                }

                //遍历同名所有服务
                for (RegistryVo item : registryVos) {
                    if(item.equals(registryVo)){
                        long registerTime1 = item.getRegisterTime();
                        if(timeMillis-registerTime1>=60000){
                            //超过60s要注销
                            registryVos.remove(registryVo);
                            //关闭定时任务
                            cancel();

                        }
                        break;
                    }

                }

            }
        },5000,3000);
        return RestResult.success().msg("注册成功！");
    }

    @ApiOperation("向注册中心注销")
    @PostMapping("/unregister")
    public RestResult unregister(@RequestBody RegistryVo registryVo){
        //其他微服务在正常下线的时候会调用此接口，从注册中心注销。
        boolean flag = registryCenter.unRegistryService(registryVo);
        if(flag){
            return RestResult.success().msg("成功注销！");
        }else return RestResult.failure().msg("注册中心不存在此服务！");
    }

    @ApiOperation("心跳")
    @PostMapping("/heartbeat")
    public RestResult heartbeat(@RequestBody RegistryVo registryVo){
        //1.记录当前时间
        long registerTime = System.currentTimeMillis();
        //2.把时间赋值给registryVo
        registryVo.setRegisterTime(registerTime);
        //3.注册中心进行心跳服务，其他微服务会每隔3s访问这个接口，此方法会重新设置registryVo中的时间。
        boolean flag = registryCenter.heartbeat(registryVo);
        if(flag){
            return RestResult.success().msg("心跳成功！");
        }else return RestResult.failure().msg("注册中心不存在此服务！");
    }


    //GET /api/discovery?name=time-service // 带负载均衡的、指定名称的应用发现.
    @ApiOperation("服务发现")
    @GetMapping("/discovery")
    public RestResult<List<RegistryVo>> discovery (@RequestParam(value = "name", required = false) String name){
        //判断name参数是否为空。
        if(name==null){
            //执行发现所有服务
            return RestResult.success().data(registryCenter.discoveryService());
        }
        //执行按照服务名返回服务。
        else {
            List<RegistryVo> registryVos = registryCenter.discoveryServiceByName(name);
            if(registryVos!=null){
                return RestResult.success().data(registryVos);
            }else return RestResult.failure().msg("该服务不存在");
        }
    }
}
