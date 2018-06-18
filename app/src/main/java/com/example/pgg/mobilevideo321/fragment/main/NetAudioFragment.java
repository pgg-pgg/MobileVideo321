package com.example.pgg.mobilevideo321.fragment.main;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.activity.MainActivity;
import com.example.pgg.mobilevideo321.adapter.NetAudioFragmentAdapter;
import com.example.pgg.mobilevideo321.base.BaseFragment;
import com.example.pgg.mobilevideo321.bean.NetMusicItem;
import com.example.pgg.mobilevideo321.bean.NetVideoItem;
import com.example.pgg.mobilevideo321.net.NetWork;
import com.example.pgg.mobilevideo321.widget.xListView.XListView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by pgg on 18-6-11.
 */

public class NetAudioFragment extends BaseFragment implements XListView.IXListViewListener {


    private Subscription mSubscription;

    private NetAudioFragmentAdapter adapter;

    @BindView(R.id.lv_net_audio)
    XListView lv_net_audio;

    @BindView(R.id.empty_view)
    View empty_view;

    @BindView(R.id.error_view)
    View error_view;

    @BindView(R.id.loading_view)
    View loading_view;

    @BindView(R.id.fb_audio)
    FloatingActionButton fb_audio;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_net_audio;
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

        lv_net_audio.setPullRefreshEnable(true);
        lv_net_audio.setPullLoadEnable(true);

        lv_net_audio.setXListViewListener(this);

    }

    /**
     * 初始化浮动按钮
     */
    private void initFab() {
        fb_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter!=null){
                    lv_net_audio.smoothScrollToPosition(0);
                }
            }
        });
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


    private void requestData() {
        unSubscribe();
        mSubscription= NetWork.getmNetMusicApi()
                .getNetMusicInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NetMusicItem>() {
                    @Override
                    public void onCompleted() {
                        loading_view.setVisibility(View.GONE);
                        error_view.setVisibility(View.GONE);
                        empty_view.setVisibility(View.GONE);
                        fb_audio.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        loading_view.setVisibility(View.GONE);
                        error_view.setVisibility(View.VISIBLE);
                        empty_view.setVisibility(View.GONE);
                        fb_audio.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(NetMusicItem netMusicItem) {
                        setDataToAdapter(netMusicItem);
                    }
                });
    }

    private void setDataToAdapter(NetMusicItem netMusicItem) {
        adapter=new NetAudioFragmentAdapter(getContext(),netMusicItem);
        lv_net_audio.setAdapter(adapter);
        onLoad();
    }

    private void unSubscribe() {
        if (mSubscription!=null&&mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }

    @Override
    protected void managerArguments() {

    }

    //获取系统时间
    private String getSystemTime() {
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    private void onLoad(){
        lv_net_audio.stopRefresh();
        lv_net_audio.stopLoadMore();
        lv_net_audio.setRefreshTime("更新时间:"+getSystemTime());
    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public void onLoadMore() {
        lv_net_audio.stopLoadMore();
    }
}
