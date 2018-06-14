package com.example.pgg.mobilevideo321.utils;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by pgg on 18-6-12.
 */

public class TimeUtils {
    private static StringBuilder mFormatBuilder=new StringBuilder();
    private static Formatter formatter=new Formatter(mFormatBuilder, Locale.getDefault());

    public static String stringForTime(int times){
        int totalSeconds=times/1000;
        int seconds=totalSeconds%60;
        int minutes=(totalSeconds/60)%60;
        int hours=totalSeconds/3600;
        mFormatBuilder.setLength(0);
        if (hours>0){
            return formatter.format("%d:%02d:%02d",hours,minutes,seconds).toString();
        }else {
            return formatter.format("%02d:%02d",minutes,seconds).toString();
        }
    }


}
