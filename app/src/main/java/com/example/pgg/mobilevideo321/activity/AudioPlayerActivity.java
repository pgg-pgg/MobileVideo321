package com.example.pgg.mobilevideo321.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pgg.mobilevideo321.IMusicPlayService;
import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.base.BaseActivity;
import com.example.pgg.mobilevideo321.service.MusicPlayerService;
import com.example.pgg.mobilevideo321.utils.LyricUtils;
import com.example.pgg.mobilevideo321.utils.StateBarTranslucentUtils;
import com.example.pgg.mobilevideo321.utils.TimeUtils;
import com.example.pgg.mobilevideo321.widget.BaseVisualizerView;
import com.example.pgg.mobilevideo321.widget.ShowLyricView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by pgg on 18-6-14.
 */

public class AudioPlayerActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {


    public static final int PROGRESS=1;
    private static final int SHOW_LYRIC = 2;
    private int position;

    @BindView(R.id.base_vis)
    BaseVisualizerView base_vis;

    @BindView(R.id.btn_audio_start_pause)
    Button btn_audio_start_pause;

    @BindView(R.id.btn_audio_play_mode)
    Button btn_audio_play_mode;

    @BindView(R.id.btn_audio_pre)
    Button btn_audio_pre;

    @BindView(R.id.btn_audio_next)
    Button btn_audio_next;

    @BindView(R.id.btn_lyrc)
    Button btn_lyrc;

    @BindView(R.id.seekbar_audio)
    SeekBar seekbar_audio;

    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.tv_artist)
    TextView tv_artist;

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.tv_lyric)
    ShowLyricView tv_lyric;

    private IMusicPlayService service;


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_LYRIC:
                    try {
                        //1得到当前播放进度
                        int currentPosition=service.getCurrentPosition();
                        //2把进度传入
                        tv_lyric.setShowNextLyric(currentPosition);
                        //3实时发送
                        removeMessages(SHOW_LYRIC);
                        sendEmptyMessage(SHOW_LYRIC);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                    break;
                case PROGRESS:
                    //得到当前进度
                    try {
                        int currentPosition=service.getCurrentPosition();
                        seekbar_audio.setProgress(currentPosition);
                        tv_time.setText(TimeUtils.stringForTime(currentPosition)+"/"+TimeUtils.stringForTime(service.getDuration()));

                        removeMessages(PROGRESS);
                        sendEmptyMessageDelayed(PROGRESS,1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };
    private ServiceConnection coon=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service= IMusicPlayService.Stub.asInterface(iBinder);
            if (service!=null){
                try {
                    if (!notification){
                        service.openAudio(position);
                    }else {
                        showViewData();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if (service!=null){
                    service.stop();
                    service=null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
    private MyReceiver receiver;
    private boolean notification;


    @Override
    public void initContentView() {
        StateBarTranslucentUtils.setStateBarTranslucent(this);
        setContentView(R.layout.activity_audio_play);
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        getData();

        bindAndStartService();
        initData();

        seekbar_audio.setOnSeekBarChangeListener(this);

    }

    private Visualizer mVisualizer;

    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi() {

        int audioSessionid = 0;
        try {
            audioSessionid = service.getAudioSessionId();
            System.out.println("audioSessionid=="+audioSessionid);
            mVisualizer = new Visualizer(audioSessionid);
            // 参数内必须是2的位数
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            // 设置允许波形表示，并且捕获它
            base_vis.setVisualizer(mVisualizer);
            mVisualizer.setEnabled(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void initData() {
        receiver=new MyReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            //拖动进度
            try {
                service.seekTo(progress);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //开始歌词同步
            showLyric();
            showViewData();
            checkPlayMode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVisualizer!=null){
            mVisualizer.release();
        }
    }

    private void showLyric(){
        LyricUtils lyricUtils=new LyricUtils();

        //传歌词文件
        try {
            String path=service.getAudioPath();

            path=path.substring(0,path.lastIndexOf("."));
            File file=new File(path+".lrc");
            if (!file.exists()){
                file=new File(path+".txt");
            }
            lyricUtils.readLyricFile(file);
            tv_lyric.setLyrics(lyricUtils.getLyrics());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (lyricUtils.isExistsLyric()){
            handler.sendEmptyMessage(SHOW_LYRIC);
        }
    }

    private void showViewData() {
        try {
            tv_artist.setText(service.getArtist());
            tv_name.setText(service.getName());
            //设置进度条的最大值
            seekbar_audio.setMax(service.getDuration());
            checkPlayMode();
            setupVisualizerFxAndUi();

            handler.sendEmptyMessage(PROGRESS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);
        if (receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
        if (coon!=null){
            unbindService(coon);
        }
        super.onDestroy();
    }

    @OnClick({R.id.btn_audio_play_mode,R.id.btn_audio_pre,R.id.btn_audio_start_pause,R.id.btn_audio_next,R.id.btn_lyrc})
    public void onnClick(View view){
        switch (view.getId()){
            case R.id.btn_audio_play_mode:
                setPlayMode();
                break;
            case R.id.btn_audio_pre:
                if (service!=null){
                    try {
                        service.pre();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_audio_start_pause:
                if (service!=null){
                    try {
                        if (service.isPlaying()){
                            //暂停
                            service.pause();
                            btn_audio_start_pause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                        }else {
                            //播放
                            service.start();
                            btn_audio_start_pause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_audio_next:
                if (service!=null){
                    try {
                        service.next();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_lyrc:
                if (tv_lyric.getVisibility()==View.VISIBLE){
                    tv_lyric.setVisibility(View.GONE);
                }else {
                    tv_lyric.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void setPlayMode() {
        try {
            int playMode=service.getPlayMode();
            if (playMode==MusicPlayerService.REPEAT_NORMAL){
                playMode=MusicPlayerService.REPEAT_SINGLE;
            }else if (playMode==MusicPlayerService.REPEAT_SINGLE){
                playMode=MusicPlayerService.REPEAT_ALL;
            }else if (playMode==MusicPlayerService.REPEAT_ALL){
                playMode=MusicPlayerService.REPEAT_NORMAL;
            }else {
                playMode=MusicPlayerService.REPEAT_NORMAL;
            }

            //保存
            service.setPlayMode(playMode);
            //设置图片
            showPlayMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlayMode() {
        try {
            int playMode = service.getPlayMode();
            if (playMode==MusicPlayerService.REPEAT_NORMAL){
                btn_audio_play_mode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(this,"顺序播放",Toast.LENGTH_LONG).show();
            }else if (playMode==MusicPlayerService.REPEAT_SINGLE){
                btn_audio_play_mode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                Toast.makeText(this,"单曲播放",Toast.LENGTH_LONG).show();
            }else if (playMode==MusicPlayerService.REPEAT_ALL){
                btn_audio_play_mode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                Toast.makeText(this,"列表循环",Toast.LENGTH_LONG).show();
            }else {
                btn_audio_play_mode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(this,"顺序播放",Toast.LENGTH_LONG).show();
            }


        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 校验状态
     */
    private void checkPlayMode() {
        try {
            int playMode = service.getPlayMode();
            if (playMode==MusicPlayerService.REPEAT_NORMAL){
                btn_audio_play_mode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }else if (playMode==MusicPlayerService.REPEAT_SINGLE){
                btn_audio_play_mode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            }else if (playMode==MusicPlayerService.REPEAT_ALL){
                btn_audio_play_mode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            }else {
                btn_audio_play_mode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }

            if (service.isPlaying()){
                btn_audio_start_pause.setBackgroundResource(R.drawable.btn_audio_start_selector);
            }else {
                btn_audio_start_pause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void bindAndStartService() {
        Intent intent=new Intent(this, MusicPlayerService.class);
        intent.setAction("com.example.pgg.mobilevideo321_OPENAUDIO");
        bindService(intent,coon, Context.BIND_AUTO_CREATE);
        startService(intent);//避免实例化多个service
    }


    private void getData() {
        notification=getIntent().getBooleanExtra("Notification",false);
        if (!notification){
            position = getIntent().getIntExtra("position", 0);
        }
    }

    @Override
    public void initPresenter() {

    }
}
