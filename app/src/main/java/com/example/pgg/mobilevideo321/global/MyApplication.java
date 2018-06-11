package com.example.pgg.mobilevideo321.global;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

import com.example.pgg.mobilevideo321.activity.MainActivity;

/**
 * Created by pgg on 18-6-11.
 */

public class MyApplication extends Application {

    private static MainActivity sMainActivity = null;
    private static MyApplication mInstance;

    /**
     * 屏幕宽度
     */
    public static int screenWidth;
    /**
     * 屏幕高度
     */
    public static int screenHeight;
    /**
     * 屏幕密度
     */
    public static float screenDensity;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initScreenSize();
    }

    public static Context getInstance() {
        return mInstance;
    }

    /**
     * 初始化当前设备屏幕宽高
     */
    private void initScreenSize() {
        DisplayMetrics curMetrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = curMetrics.widthPixels;
        screenHeight = curMetrics.heightPixels;
        screenDensity = curMetrics.density;
    }
    public static void setMainActivity(MainActivity activity) {
        sMainActivity = activity;
    }
}