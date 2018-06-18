package com.example.pgg.mobilevideo321.net.api;


import com.example.pgg.mobilevideo321.bean.NetMusicItem;
import com.example.pgg.mobilevideo321.bean.NetVideoItem;

import retrofit2.http.GET;
import rx.Observable;

/**
 * 获取种类的api接口
 */


public interface NetMusicApi {
    @GET("0-20.json?market=baidu&udid=863425026599592&appname=baisibudejie&os=4.2.2&client=android&visiting=&mac=98%3A6c%3Af5%3A4b%3A72%3A6d&ver=6.2.8")
    Observable<NetMusicItem> getNetMusicInfo();
}
