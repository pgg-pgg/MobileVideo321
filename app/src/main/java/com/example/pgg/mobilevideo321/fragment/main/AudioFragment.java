package com.example.pgg.mobilevideo321.fragment.main;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.activity.AudioPlayerActivity;
import com.example.pgg.mobilevideo321.activity.MainActivity;
import com.example.pgg.mobilevideo321.activity.SystemVideoPlayer;
import com.example.pgg.mobilevideo321.adapter.VideoFragmentAdapter;
import com.example.pgg.mobilevideo321.base.BaseFragment;
import com.example.pgg.mobilevideo321.bean.MediaItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by pgg on 18-6-11.
 */

public class AudioFragment extends BaseFragment implements AdapterView.OnItemClickListener {


    @BindView(R.id.lv_video)
    ListView lv_video;

    @BindView(R.id.empty_view)
    View empty_view;

    @BindView(R.id.fb_video)
    FloatingActionButton fb_video;

    @BindView(R.id.loading_view)
    View loading_view;

    private List<MediaItem> mediaItems;
    private VideoFragmentAdapter adapter;

    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems!=null&&mediaItems.size()>0){
                //获取到数据
                //设置适配器
                //empty_view隐藏
                empty_view.setVisibility(View.GONE);
                fb_video.setVisibility(View.VISIBLE);
                adapter=new VideoFragmentAdapter(getActivity(),mediaItems,false);
                lv_video.setAdapter(adapter);

            }else {
                //没有数据
                //empty_view显示
                empty_view.setVisibility(View.VISIBLE);
                fb_video.setVisibility(View.GONE);
            }
            loading_view.setVisibility(View.GONE);
        }
    };

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_video;
    }

    @Override
    public void initView() {
        initFab();

        getDataFromLocal();
        lv_video.setOnItemClickListener(this);
    }

    /**
     * 从本地sdcard获取视频
     * 1.遍历sdcard，根据后缀名便利
     * 2.从内容提供者获取
     */
    private void getDataFromLocal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = getActivity().getContentResolver();
                Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs={
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Audio.Media.DURATION,//视频的总时长
                        MediaStore.Audio.Media.SIZE,//视频文件的大小
                        MediaStore.Audio.Media.DATA,//视频的绝对地址
                        MediaStore.Audio.Media.ARTIST//如果是歌曲的话，表示歌曲的作者
                };
                Cursor query = contentResolver.query(uri, objs, null, null, null);
                if (query!=null){
                    mediaItems=new ArrayList<>();
                    while (query.moveToNext()){
                        MediaItem mediaItem=new MediaItem();
                        String name=query.getString(0);
                        long duration=query.getLong(1);
                        long size=query.getLong(2);
                        String data=query.getString(3);
                        String artist=query.getString(4);
                        mediaItem.setArtist(artist);
                        mediaItem.setData(data);
                        mediaItem.setDuration(duration);
                        mediaItem.setName(name);
                        mediaItem.setSize(size);
                        mediaItems.add(mediaItem);
                    }
                    query.close();
                }
                //跳到主线程
                handler.sendEmptyMessage(10);
            }
        }).start();
    }

    /**
     * 初始化浮动按钮
     */
    private void initFab() {
        fb_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter!=null){
                    lv_video.smoothScrollToPosition(0);
                }
            }
        });
    }

    @Override
    protected void managerArguments() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(getActivity(), AudioPlayerActivity.class);
        intent.putExtra("position",position);
        getContext().startActivity(intent);
    }
}
