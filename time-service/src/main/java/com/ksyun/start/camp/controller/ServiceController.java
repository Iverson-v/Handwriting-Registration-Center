package com.ksyun.start.camp.controller;

import com.ksyun.start.camp.entity.RestResult;
import com.ksyun.start.camp.entity.TimeVo;
import com.ksyun.start.camp.service.RegistryClient;
import com.ksyun.start.camp.service.SimpleTimeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Api(tags="时间服务")
public class ServiceController {
    @Autowired
    private SimpleTimeService simpleTimeService;
    //注入注册中心客户端
    @Autowired
    private RegistryClient registryClient;

    // 在此实现简单时间服务的接口逻辑，获取当前日期时间信息及节点信息。
    //GET /api/getDateTime?style=full
    @GetMapping("/getDateTime")
    @ApiOperation("获取时间")
    public RestResult<TimeVo> getDataTime(@RequestParam(value = "style", required = true) String style){
        //1.获取时间格式
        String dateTime = simpleTimeService.getDateTime(style);
        //2.获取服务id
        String serviceId = registryClient.getServiceId();
        //3.封装vo对象出参
        TimeVo timeVo=new TimeVo();
        timeVo.setResult(dateTime);
        timeVo.setServiceId(serviceId);
        return RestResult.success().data(timeVo);

    }

}
