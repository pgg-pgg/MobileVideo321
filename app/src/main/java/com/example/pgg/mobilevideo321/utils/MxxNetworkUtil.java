package com.example.pgg.mobilevideo321.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by pgg on 2018/5/12.
 */

public class MxxNetworkUtil {

    /**
     * 检测网络是否可用
     * @param context 上下文
     * @return true 表示有网络连接 false表示没有可用网络连接
     */
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivity=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity==null){
            return false;
        }else {
            NetworkInfo[] info=connectivity.getAllNetworkInfo();
            if (info!=null){
                for (int i=0;i<info.length;i++){
                    if (info[i].getState()==NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 用于判断是否是wifi网络
     * @param context
     * @return
     */
    public static boolean isWifiConnect(Context context){
        ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if (networkInfo!=null&&networkInfo.getType()==ConnectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }
}
