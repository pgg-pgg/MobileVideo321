package com.example.pgg.mobilevideo321.net;

import android.os.Environment;

import com.example.pgg.mobilevideo321.constant.Constant;
import com.example.pgg.mobilevideo321.global.MyApplication;
import com.example.pgg.mobilevideo321.net.api.NetMusicApi;
import com.example.pgg.mobilevideo321.net.api.NetVideoApi;
import com.example.pgg.mobilevideo321.utils.MxxNetworkUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pgg on 18-6-14.
 */

public class NetWork {

    private static NetVideoApi mNetVideoApi;
    private static NetMusicApi mNetMusicApi;



    private static final long cacheSize = 1024 * 1024 * 20;//缓存文件大小
    private static String cacheDirectory = Environment.getExternalStorageState() + "/okhttpcaches";
    private static Cache cache = new Cache(new File(cacheDirectory), cacheSize);
    private static final OkHttpClient cacheClient;


    public static Interceptor provideOfflineCacheInterceptor () {
        return new Interceptor()
        {
            @Override
            public Response intercept (Interceptor.Chain chain) throws IOException
            {
                Request request = chain.request();

                /**
                 * 未联网获取缓存数据
                 */
                if (!MxxNetworkUtil.isNetworkAvailable(MyApplication.getInstance()))
                {
                    //在20秒缓存有效，此处测试用，实际根据需求设置具体缓存有效时间
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(20, TimeUnit.SECONDS )
                            .build();

                    request = request.newBuilder()
                            .cacheControl( cacheControl )
                            .build();
                }

                return chain.proceed( request );
            }
        };
    }

    public static Interceptor provideCacheInterceptor () {
        return new Interceptor()
        {
            @Override
            public Response intercept (Chain chain) throws IOException
            {
                Response response = chain.proceed( chain.request() );

                // re-write response header to force use of cache
                // 正常访问同一请求接口（多次访问同一接口），给30秒缓存，超过时间重新发送请求，否则取缓存数据
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(30, TimeUnit.SECONDS )
                        .build();

                return response.newBuilder()
                        .header( "cache-control", cacheControl.toString() )
                        .build();
            }
        };
    }
    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(8, TimeUnit.SECONDS);
        builder.writeTimeout(8, TimeUnit.SECONDS);
        builder.readTimeout(8, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        builder.addInterceptor( provideOfflineCacheInterceptor());
        builder.addNetworkInterceptor( provideCacheInterceptor());
        builder.cache(cache);
        //添加日志信息拦截器
        builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        cacheClient = builder.build();
    }

    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

    public static NetVideoApi getmNetVideoApi(){
        if (mNetVideoApi==null){
            Retrofit retrofit=new Retrofit.Builder()
                    .client(cacheClient)
                    .baseUrl(Constant.NET_VEDIO_ADDRESS)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            mNetVideoApi=retrofit.create(NetVideoApi.class);
        }
        return mNetVideoApi;
    }

    public static NetMusicApi getmNetMusicApi(){
        if (mNetMusicApi==null){
            Retrofit retrofit=new Retrofit.Builder()
                    .client(cacheClient)
                    .baseUrl(Constant.ALL_RES_URL)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            mNetMusicApi=retrofit.create(NetMusicApi.class);
        }
        return mNetMusicApi;
    }
}
