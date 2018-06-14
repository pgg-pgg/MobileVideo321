package com.example.pgg.mobilevideo321.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.activity.SystemVideoPlayer;
import com.example.pgg.mobilevideo321.adapter.NetVideoFragmentAdapter;
import com.example.pgg.mobilevideo321.base.BaseFragment;
import com.example.pgg.mobilevideo321.bean.MediaItem;
import com.example.pgg.mobilevideo321.bean.NetVideoItem;
import com.example.pgg.mobilevideo321.net.NetWork;
import com.example.pgg.mobilevideo321.widget.xListView.XListView;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by pgg on 18-6-11.
 */

public class NetVideoFragment extends BaseFragment implements XListView.IXListViewListener {

    @BindView(R.id.lv_net_video)
    XListView lv_net_video;

    @BindView(R.id.empty_view)
    View empty_view;

    @BindView(R.id.error_view)
    View error_view;

    @BindView(R.id.fb_video)
    FloatingActionButton fb_video;

    @BindView(R.id.loading_view)
    View loading_view;

    private NetVideoFragmentAdapter adapter;

    private Subscription mSubscription;

    private List<MediaItem> mediaItems;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_net_video;
    }

    @Override
    public void initView() {
        //初始化右下角浮动按钮
        initFab();

        //初始化加载空|错误的view
        initEmptyView();

        //启动时显示正在加载页面
        loading_view.setVisibility(View.VISIBLE);

        //请求数据
        requestData();

        lv_net_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //调用自己的播放器,显式意图,传递列表
                Intent intent=new Intent(getActivity(), SystemVideoPlayer.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("mediaItems", (Serializable) mediaItems);
                intent.putExtras(bundle);
                intent.putExtra("position",position);
                getContext().startActivity(intent);
            }
        });
        lv_net_video.setPullRefreshEnable(true);
        lv_net_video.setPullLoadEnable(true);

        lv_net_video.setXListViewListener(this);
    }

    private void requestData() {
        unSubscribe();
        mSubscription= NetWork.getmNetVideoApi()
                .getNetVideoInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NetVideoItem>() {
                    @Override
                    public void onCompleted() {
                        loading_view.setVisibility(View.GONE);
                        error_view.setVisibility(View.GONE);
                        empty_view.setVisibility(View.GONE);
                        fb_video.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        loading_view.setVisibility(View.GONE);
                        error_view.setVisibility(View.VISIBLE);
                        empty_view.setVisibility(View.GONE);
                        fb_video.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(NetVideoItem netVideoItem) {
                        setDataToAdapter(netVideoItem);
                    }
                });
    }


    private void setDataToAdapter(NetVideoItem netVideoItem) {
        mediaItems=new ArrayList<>();
        for (int i=0;i<netVideoItem.getTrailers().size();i++){
            MediaItem item=new MediaItem();
            item.setDuration(netVideoItem.getTrailers().get(i).getVideoLength());
            item.setName(netVideoItem.getTrailers().get(i).getVideoTitle());
            item.setData(netVideoItem.getTrailers().get(i).getHightUrl());
            mediaItems.add(item);
        }
        adapter=new NetVideoFragmentAdapter(getActivity(),netVideoItem.getTrailers());
        lv_net_video.setAdapter(adapter);
        onLoad();
    }

    private void unSubscribe() {
        if (mSubscription!=null&&mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }

    private void initEmptyView() {
        /**
         * 网络请求失败没有数据
         */
        empty_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        /**
         * 网络请求错误 | 没有网络
         */
        error_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();
            }
        });
    }

    /**
     * 初始化浮动按钮
     */
    private void initFab() {
        fb_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter!=null){
                    lv_net_video.smoothScrollToPosition(0);
                }
            }
        });
    }

    //获取系统时间
    private String getSystemTime() {
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    private void onLoad(){
        lv_net_video.stopRefresh();
        lv_net_video.stopLoadMore();
        lv_net_video.setRefreshTime("更新时间:"+getSystemTime());
    }


    @Override
    protected void managerArguments() {

    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public void onLoadMore() {
        lv_net_video.stopLoadMore();
    }
}
