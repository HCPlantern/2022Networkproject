package com.nju.HttpClient.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeTransformer {
//    实现时间戳和标准时间的互转
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
    public static long getTimestamp(String time) throws ParseException {
        return simpleDateFormat.parse(time).getTime();
    }

    public static String toTimeString(long timestamp){
        return simpleDateFormat.format(new Date(timestamp));
    }
}
