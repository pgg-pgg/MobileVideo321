package com.example.pgg.mobilevideo321.fragment.main;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.adapter.VideoFragmentAdapter;
import com.example.pgg.mobilevideo321.base.BaseFragment;
import com.example.pgg.mobilevideo321.bean.MediaItem;
import com.example.pgg.mobilevideo321.global.MyApplication;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by pgg on 18-6-11.
 */

public class VideoFragment extends BaseFragment {

    private Context context;
    @BindView(R.id.lv_video)
    ListView lv_video;

    @BindView(R.id.empty_view)
    View empty_view;

    @BindView(R.id.fb_video)
    FloatingActionButton fb_video;

    public VideoFragment newInstance(Context context){
        this.context=context;
        VideoFragment fragment=new VideoFragment();
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_video;
    }

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
                adapter=new VideoFragmentAdapter(context,mediaItems);
                lv_video.setAdapter(adapter);

            }else {
                //没有数据
                //empty_view显示
                empty_view.setVisibility(View.VISIBLE);

            }
        }
    };

    @Override
    public void initView() {
        initFab();

        getDataFromLocal();
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
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs={
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Video.Media.DURATION,//视频的总时长
                        MediaStore.Video.Media.SIZE,//视频文件的大小
                        MediaStore.Video.Media.DATA,//视频的绝对地址
                        MediaStore.Video.Media.ARTIST//如果是歌曲的话，表示歌曲的作者
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
}
