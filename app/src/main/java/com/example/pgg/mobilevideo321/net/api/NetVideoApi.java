package com.example.pgg.mobilevideo321.net.api;


import com.example.pgg.mobilevideo321.bean.NetVideoItem;

import retrofit2.http.GET;
import rx.Observable;

/**
 * 获取种类的api接口
 */


public interface NetVideoApi {
    @GET("TrailerList.api")
    Observable<NetVideoItem> getNetVideoInfo();
}
