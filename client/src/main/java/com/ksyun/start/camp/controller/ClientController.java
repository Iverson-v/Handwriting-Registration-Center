package com.ksyun.start.camp.controller;

import com.ksyun.start.camp.entity.*;
import com.ksyun.start.camp.service.ClientService;
import com.ksyun.start.camp.service.TimeService;
import com.ksyun.start.camp.utils.jaksonutils.JacksonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 默认的客户端 API Controller
 */
@RestController
@RequestMapping("/api")
@Api(tags="msa服务")
public class ClientController {

    @Autowired
    private TimeService timeService;
    // 在这里开始编写你的相关接口实现代码
    // 返回值对象使用 ApiResponse 类

    @Autowired
    private ClientService clientService;

    // 提示：调用 ClientService
    @GetMapping("/getInfo")
    @ApiOperation("获取客户端信息")
    public RestResult getDataTime(){
        //1.调用远程接口得到的json字符串
        String jsonStr = timeService.getDateTime("full");

        //2.创建返回对象，
        ApiResponse apiResponse;
        if(jsonStr!=null){
            //如果服务可用，转换回对象
            ResponseTimeVo responseTimeVo = JacksonUtil.toBean(jsonStr, ResponseTimeVo.class);
            TimeVo timeVo = responseTimeVo.getData();

            apiResponse = new ApiResponse(null,"");

            //把时间转换为北京时间。
            String gmtTimeString = timeVo.getResult();
            ZonedDateTime gmtDateTime = LocalDateTime.parse(gmtTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.of("GMT"));

            // 将 GMT 时间转化为北京时间
            ZonedDateTime beijingDateTime = gmtDateTime.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));

            // 将北京时间转化为字符串
            String beijingTimeString = beijingDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            //拼接最后返回结果。
            String result="Hello Kingsoft Clound Star Camp - ["+clientService.getServiceId()+"] - "+beijingTimeString;
            //apiResponse.setData(result);
            return RestResult.success().data(result);
        }else {
            //如果服务不可用
            //apiResponse = new ApiResponse("服务不可用！",null);
            return RestResult.failure().msg("服务不可用");
        }


    }
}
