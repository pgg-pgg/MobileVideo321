package com.example.pgg.mobilevideo321.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.bean.MediaItem;
import com.example.pgg.mobilevideo321.global.MyApplication;
import com.example.pgg.mobilevideo321.utils.TimeUtils;
import com.example.pgg.mobilevideo321.widget.MyVideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pgg on 18-6-12.
 */

public class SystemVideoPlayer extends Activity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    private boolean isUseSystem=false;

    private final static int PROGRESS=1;
    private static final int DEFAULT_SCREEN = 2;
    private Uri uri;

    private MyVideoView video_view;
    private LinearLayout mLl_top;
    private TextView mTv_name;
    private ImageView mIv_battery;
    private TextView mTv_system_time;
    private Button mBtn_voice;
    private SeekBar mSeekbar_voice;
    private Button mBtn_switch_player;
    private LinearLayout mLl_bottom;
    private TextView mTv_current_time;
    private SeekBar mSeekbar_video;
    private TextView mTv_duration;
    private Button mBtn_exit;
    private Button mBtn_video_pre;
    private Button mBtn_video_start_pause;
    private Button mBtn_video_next;
    private Button mBtn_video_switch_screen;
    private View mMedia_controller;

    private MyReceiver receiver;
    private ArrayList<MediaItem> mediaItems;
    private int position;
    private boolean isshowMediaController=false;

    private GestureDetector detector;
    private boolean isFullScreen;
    private final static int FULL_SCREEN=1;
    private int videoWidth;
    private int videoHeight;

    private AudioManager am;
    private int currentVoice;
    private int maxVoice;
    private boolean isMute=false;
    private boolean isNetUri;
    private LinearLayout ll_buffer;
    private TextView tv_netspeed;

    private int preCurrentPosition;
    private TextView tv_loading_netspeed;
    private LinearLayout ll_loading;
    private long lastTotalRxBytes;
    private long lastTimeStamp;

    // End Of Content View Elements

    private void bindViews() {
        setContentView(R.layout.activity_system_video);
        mMedia_controller=findViewById(R.id.media_controller);
        video_view=findViewById(R.id.video_view);
        mLl_top = (LinearLayout) findViewById(R.id.ll_top);
        mTv_name = (TextView) findViewById(R.id.tv_name);
        mIv_battery = (ImageView) findViewById(R.id.iv_battery);
        mTv_system_time = (TextView) findViewById(R.id.tv_system_time);
        mBtn_voice = (Button) findViewById(R.id.btn_voice);
        mSeekbar_voice = (SeekBar) findViewById(R.id.seekbar_voice);
        mBtn_switch_player = (Button) findViewById(R.id.btn_switch_player);
        mLl_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        mTv_current_time = (TextView) findViewById(R.id.tv_current_time);
        mSeekbar_video = (SeekBar) findViewById(R.id.seekbar_video);
        mTv_duration = (TextView) findViewById(R.id.tv_duration);
        mBtn_exit = (Button) findViewById(R.id.btn_exit);
        mBtn_video_pre = (Button) findViewById(R.id.btn_video_pre);
        mBtn_video_start_pause = (Button) findViewById(R.id.btn_video_start_pause);
        mBtn_video_next = (Button) findViewById(R.id.btn_video_next);
        mBtn_video_switch_screen = (Button) findViewById(R.id.btn_video_switch_screen);

        ll_buffer = findViewById(R.id.ll_buffer);
        tv_netspeed=findViewById(R.id.tv_netspeed);

        tv_loading_netspeed=findViewById(R.id.tv_loading_netspeed);
        ll_loading=findViewById(R.id.ll_loading);

        mBtn_voice.setOnClickListener(this);
        mBtn_switch_player.setOnClickListener(this);
        mBtn_exit.setOnClickListener(this);
        mBtn_video_pre.setOnClickListener(this);
        mBtn_video_next.setOnClickListener(this);
        mBtn_video_start_pause.setOnClickListener(this);
        mBtn_video_switch_screen.setOnClickListener(this);

        //更新网速
        handler.sendEmptyMessage(101);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViews();
        setData();
        setListener();

        //得到播放地址
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("mediaItems");
        position=getIntent().getIntExtra("position",0);
        uri=getIntent().getData();

        if (mediaItems!=null&&mediaItems.size()>0){
            MediaItem mediaItem=mediaItems.get(position);
            mTv_name.setText(mediaItem.getName());
            isNetUri= isNetUri(mediaItem.getData());
            video_view.setVideoPath(mediaItem.getData());
        }else if (uri!=null){
            mTv_name.setText(uri.toString());
            isNetUri= isNetUri(uri.toString());
            video_view.setVideoURI(uri);
        }else {
            Toast.makeText(SystemVideoPlayer.this,"列表暂无视频",Toast.LENGTH_LONG).show();
        }
        setButtonState();

        am= (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice=am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mSeekbar_voice.setProgress(currentVoice);
        mSeekbar_voice.setMax(maxVoice);

        detector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            //长按事件
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                startOrPause();
            }

            //双击
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isFullScreen){
                    //默认
                    setVideoType(DEFAULT_SCREEN);
                }else {
                    //全屏
                    setVideoType(FULL_SCREEN);
                }
                return super.onDoubleTap(e);
            }

            //单击
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isshowMediaController){
                    hideMediaController();
                    handler.removeMessages(100);
                }else {
                    showMediaController();
                    handler.sendEmptyMessageDelayed(100,4000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    private void setVideoType(int type) {
        switch (type){
            case FULL_SCREEN:
                //设置视频大小
                //设置按钮的状态
                isFullScreen=true;
                video_view.setVideoSize(MyApplication.screenWidth,MyApplication.screenHeight);
                mBtn_video_switch_screen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
                break;
            case DEFAULT_SCREEN:
                int mVideoWidth=videoWidth;
                int mVideoHeight=videoHeight;

                int width=MyApplication.screenWidth;
                int height=MyApplication.screenHeight;
                if (mVideoWidth*width<height*mVideoHeight){
                    width=height*mVideoWidth/mVideoHeight;
                }else if (mVideoWidth*height>width*mVideoHeight){
                    height=width*mVideoHeight/mVideoWidth;
                }
                isFullScreen=false;
                video_view.setVideoSize(width,height);
                mBtn_video_switch_screen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                break;
        }
    }


    private void hideMediaController(){
        mMedia_controller.setVisibility(View.GONE);
        isshowMediaController=false;
    }

    private void showMediaController(){
        mMedia_controller.setVisibility(View.VISIBLE);
        isshowMediaController=true;
    }


    private float startY;
    //屏幕高
    private float touchRang;

    //记录当前音量
    private int mVol;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //按下，记录值
                startY=event.getY();
                mVol=am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang=Math.min(MyApplication.screenWidth,MyApplication.screenHeight);
                handler.removeMessages(100);
                break;

            case MotionEvent.ACTION_MOVE:
                float endY=event.getY();
                float distanceY=startY-endY;
                //改变声音=（滑动屏幕的距离/总距离)*最大音量
                float delta=(distanceY/touchRang)*maxVoice;
                //最终声音=原来的+增加的
                int voice= (int) Math.min(Math.max(mVol+delta,0),maxVoice);
                if(delta!=0){
                    isMute=false;
                    updateVoice(voice,isMute);
                }
                break;

            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(100,4000);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setData() {
        receiver=new MyReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver,intentFilter);
    }


    class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int level=intent.getIntExtra("level",0);
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level<=0){
            mIv_battery.setImageResource(R.drawable.ic_battery_0);
        }else if (level<=10){
            mIv_battery.setImageResource(R.drawable.ic_battery_10);
        }else if (level<=20){
            mIv_battery.setImageResource(R.drawable.ic_battery_20);
        }else if (level<=40){
            mIv_battery.setImageResource(R.drawable.ic_battery_40);
        }else if (level<=60){
            mIv_battery.setImageResource(R.drawable.ic_battery_60);
        }else if (level<=80){
            mIv_battery.setImageResource(R.drawable.ic_battery_80);
        }else if (level<=100){
            mIv_battery.setImageResource(R.drawable.ic_battery_100);
        }else {
            mIv_battery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    @Override
    protected void onDestroy() {
        if (receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
        super.onDestroy();
    }

    private void setListener() {
        //播放器准备好监听
        video_view.setOnPreparedListener(this);
        //播放出错监听
        video_view.setOnErrorListener(this);
        //播放完成监听
        video_view.setOnCompletionListener(this);
        mSeekbar_video.setOnSeekBarChangeListener(this);
        mSeekbar_voice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());

        //监听视频播放不流畅
        if (isUseSystem){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
                video_view.setOnInfoListener(new MyOnInfoListener());
            }
        }

    }

    //当底层解码准备好
    @Override
    public void onPrepared(MediaPlayer mp) {
        videoWidth=mp.getVideoWidth();
        videoHeight=mp.getVideoHeight();
        video_view.start();//开始播放
        int duration=video_view.getDuration();
        mSeekbar_video.setMax(duration);
        mTv_duration.setText(TimeUtils.stringForTime(duration));

        hideMediaController();

        handler.sendEmptyMessage(PROGRESS);

        setVideoType(DEFAULT_SCREEN);

        ll_loading.setVisibility(View.GONE);

        //监听拖动完成
        mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(SystemVideoPlayer.this,"播放出错！",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNextVideo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_voice:
                isMute=!isMute;
                updateVoice(currentVoice,isMute);
                break;
            case R.id.btn_switch_player:

                break;
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_video_pre:
                playPreVideo();
                break;
            case R.id.btn_video_start_pause:
                startOrPause();
                break;
            case R.id.btn_video_next:
                playNextVideo();
                break;
            case R.id.btn_video_switch_screen:
                if (isFullScreen){
                    setVideoType(DEFAULT_SCREEN);
                }else {
                    setVideoType(FULL_SCREEN);
                }
                break;
        }

        handler.removeMessages(100);
        handler.sendEmptyMessageDelayed(100,4000);
    }

    private void startOrPause() {
        if (video_view.isPlaying()){
            //视频在播放
            mBtn_video_start_pause.setBackgroundResource(R.drawable.btn_video_start_selector);
            video_view.pause();
        }else {
            video_view.start();
            mBtn_video_start_pause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if (mediaItems!=null&&mediaItems.size()>0){
            //播放下一个
            position--;
            if (position>=0){
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                mTv_name.setText(mediaItem.getName());
                isNetUri= isNetUri(mediaItem.getData());
                video_view.setVideoPath(mediaItem.getData());
                //设置按钮状态
                setButtonState();
            }
        }else if (uri!=null){
            //上下按钮设置为灰
            setButtonState();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if (mediaItems!=null&&mediaItems.size()>0){
            //播放下一个
            position++;
            if (position<mediaItems.size()){
                MediaItem mediaItem = mediaItems.get(position);
                mTv_name.setText(mediaItem.getName());
                isNetUri= isNetUri(mediaItem.getData());
                video_view.setVideoPath(mediaItem.getData());
                ll_loading.setVisibility(View.VISIBLE);
                //设置按钮状态
                setButtonState();
            }
        }else if (uri!=null){
            //上下按钮设置为灰
            setButtonState();
        }
    }

    /**
     * 设置播放，上/下键状态
     */
    private void setButtonState() {
        if (mediaItems!=null&&mediaItems.size()>0){
            if (mediaItems.size()==1){
                mBtn_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
                mBtn_video_pre.setEnabled(false);
                mBtn_video_next.setBackgroundResource(R.drawable.btn_next_gray);
                mBtn_video_next.setEnabled(false);
            }else if (mediaItems.size()==2){
                if (position==0){
                    mBtn_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
                    mBtn_video_pre.setEnabled(false);
                    mBtn_video_next.setBackgroundResource(R.drawable.btn_video_next_selector);
                    mBtn_video_next.setEnabled(true);
                }else if (position==mediaItems.size()-1){
                    mBtn_video_next.setBackgroundResource(R.drawable.btn_next_gray);
                    mBtn_video_next.setEnabled(false);
                    mBtn_video_pre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    mBtn_video_pre.setEnabled(true);
                }else {
                    mBtn_video_pre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    mBtn_video_pre.setEnabled(true);
                    mBtn_video_next.setBackgroundResource(R.drawable.btn_video_next_selector);
                    mBtn_video_next.setEnabled(true);
                }
            }else{
                if (position==0){
                    mBtn_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
                    mBtn_video_pre.setEnabled(false);
                }else if (position==mediaItems.size()-1){
                    mBtn_video_next.setBackgroundResource(R.drawable.btn_next_gray);
                    mBtn_video_next.setEnabled(false);
                }else {
                    mBtn_video_pre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    mBtn_video_pre.setEnabled(true);
                    mBtn_video_next.setBackgroundResource(R.drawable.btn_video_next_selector);
                    mBtn_video_next.setEnabled(true);
                }
            }

        }else if (uri!=null){
            mBtn_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
            mBtn_video_pre.setEnabled(false);
            mBtn_video_next.setBackgroundResource(R.drawable.btn_next_gray);
            mBtn_video_next.setEnabled(false);
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    int currentPosition=video_view.getCurrentPosition();
                    mSeekbar_video.setProgress(currentPosition);
                    //每秒更新一次
                    mTv_current_time.setText(TimeUtils.stringForTime(currentPosition));
                    mTv_system_time.setText(getSystemTime());

                    //缓冲进度的更新
                    if (isNetUri){
                        //网络视频需要缓冲
                        int bufferPercentage = video_view.getBufferPercentage();
                        int totalBuffer=bufferPercentage*mSeekbar_video.getMax();
                        int secondaryProgress=totalBuffer/100;
                        mSeekbar_video.setSecondaryProgress(secondaryProgress);
                    }else {
                        //本地视频无缓冲效果
                        mSeekbar_video.setSecondaryProgress(0);
                    }

                    //监听卡顿
                    if (!isUseSystem){
                        if (video_view.isPlaying()){
                            int buffer=currentPosition-preCurrentPosition;
                            if (buffer<500){
                                //卡顿
                                ll_buffer.setVisibility(View.VISIBLE);
                            }else {
                                //流畅
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }

                    preCurrentPosition=currentPosition;
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS,1000);
                    break;
                case 100:
                    hideMediaController();
                    break;

                case 101:
                    //显示网速
                    String netSpeed=getNetSpeed();
                    tv_loading_netspeed.setText("玩命加载中..."+netSpeed);
                    tv_netspeed.setText("视频缓冲中..."+netSpeed);

                    removeMessages(101);
                    sendEmptyMessageDelayed(101,2000);
                    break;
            }
        }
    };

    //获取系统时间
    private String getSystemTime() {
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    //当手指滑动
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            video_view.seekTo(progress);
        }
    }

    //当手指触碰后调用
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeMessages(100);
    }

    //手指离开
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.sendEmptyMessageDelayed(100,4000);
    }

    /**
     * 声音seekbar监听
     */
    private class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                if (progress>0){
                    isMute=false;
                }else {
                    isMute=true;
                }
                updateVoice(progress,isMute);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(100);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(100,4000);
        }
    }

    /**
     * 更新音量
     * @param progress
     * @param isMute
     */
    private void updateVoice(int progress,boolean isMute) {
        if (isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            mSeekbar_voice.setProgress(0);
        }else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            mSeekbar_voice.setProgress(progress);
            currentVoice=progress;
        }
    }

    /**
     * 监听音量键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            updateVoice(currentVoice,false);
            handler.removeMessages(100);
            handler.sendEmptyMessageDelayed(100,4000);
            return true;
        }else if (keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice++;
            updateVoice(currentVoice,false);
            handler.removeMessages(100);
            handler.sendEmptyMessageDelayed(100,4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 判断是否是网络资源
     * @param uri
     * @return
     */
    public boolean isNetUri(String uri){
        boolean result=false;

        if (uri!=null){
            if (uri.toLowerCase().startsWith("http")||
                    uri.toLowerCase().startsWith("rtsp")
                    ||uri.toLowerCase().startsWith("mms")){
                result=true;
            }
        }
        return result;
    }


    private class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    //视频开始卡
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    //缓冲完成
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
            }
            return false;
        }
    }


    /**
     * 获取网络速度
     * 每两秒调用一次
     * @return
     */
    private String getNetSpeed(){
        String netSpeed;
        long nowTotalRxBytes= TrafficStats.getUidRxBytes(getApplicationInfo().uid)==TrafficStats.UNSUPPORTED?0:(TrafficStats.getTotalRxBytes()/1024);
        long nowTimeStamp=System.currentTimeMillis();
        long speed=((nowTotalRxBytes-lastTotalRxBytes)*1000/(nowTimeStamp-lastTimeStamp));

        lastTimeStamp=nowTimeStamp;
        lastTotalRxBytes=nowTotalRxBytes;
        netSpeed=String.valueOf(speed)+" kb/s";
        return netSpeed;
    }


}
