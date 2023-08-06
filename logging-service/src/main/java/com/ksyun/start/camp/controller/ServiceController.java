package com.ksyun.start.camp.controller;

import com.ksyun.start.camp.entity.LogVo;
import com.ksyun.start.camp.entity.RestResult;
import com.ksyun.start.camp.service.LoggingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实现日志服务 API
 */
@RestController
@RequestMapping("/api")
@Api(tags="时间服务")
public class ServiceController {
    @Autowired
    private LoggingService loggingService;

    //接收客户端提交的日志记录请求，并将其保存于服务内部的存储中。
    @PostMapping("/logging")
    @ApiOperation("添加日志")
    public RestResult logging(@RequestBody LogVo logVo){
        boolean flag = loggingService.logging(logVo);
        if(flag){
            return RestResult.success().msg("添加日志成功!");
        }else return RestResult.failure().msg("该日志已存在！");

    }



    //此 API 带有一个可选参数 service，其代表指定的服务ID。如果此参数存在，则列表中的数据按服务id过滤，此列表默认显示最后的5条记录（注意排序）。
    //如果不带可选参数 service，则显示全部记录，按记录 logId 倒序排列
    @GetMapping("/list")
    @ApiOperation("获取日志")
    public RestResult listLogs(@RequestParam(value = "service", required = false) String serviceId) {
        if (serviceId != null) {
            // 如果serviceId不为null，返回特定serviceId的日志
            List<LogVo> logsByServiceId = loggingService.getLogsByServiceId(serviceId);
            if (logsByServiceId==null){
                return RestResult.failure().msg("该服务id在日志中未找到！");
            }else return RestResult.success().data(logsByServiceId);

        } else {
            // 如果serviceId为null，返回所有日志
            return RestResult.success().data(loggingService.getAllLogs());
        }
    }


}
