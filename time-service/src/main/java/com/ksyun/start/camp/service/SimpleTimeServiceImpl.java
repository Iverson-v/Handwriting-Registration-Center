package com.ksyun.start.camp.service;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 代表简单时间服务实现
 */
@Component
public class SimpleTimeServiceImpl implements SimpleTimeService {




    //此接口接受一个参数，style，决定返回何种样式的日期格式，其可能的取值为：
    //- full - 完整格式，形如 2023-07-25 12:34:56
    //- date - 只含有日期部分，如 2023-07-25
    //- time - 只含有时间部分，如 12:34:56
    //- unix - Unix 时间戳（带毫秒），如 1690251417000
    //此接口返回的时间，时区都是格林尼治标准时间（GMT）
    @Override
    public String getDateTime(String style) {

        Calendar calendar= Calendar.getInstance();

        // 开始编写简单时间服务的核心逻辑
        // 获取时间、格式化时间、返回
        if("full".equals(style)){
            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
            return dateFormat.format(calendar.getTime());
            //System.out.println(dateFormat.format(calendar.getTime()));
        } else if ("date".equals(style)) {
            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
            return dateFormat.format(calendar.getTime());
        } else if ("time".equals(style)) {
            SimpleDateFormat dateFormat= new SimpleDateFormat("HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
            return dateFormat.format(calendar.getTime());
        } else if ("unix".equals(style)) {
            long epochMilli = System.currentTimeMillis();//时间戳是以GMT为准，不会变的。
            return String.valueOf(epochMilli);
        }else
            return "输入时间格式有误，请输入正确的参数，如full，date等！";

    }
}
