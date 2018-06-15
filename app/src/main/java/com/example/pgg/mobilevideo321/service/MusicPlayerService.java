package com.example.pgg.mobilevideo321.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.pgg.mobilevideo321.IMusicPlayService;
import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.activity.AudioPlayerActivity;
import com.example.pgg.mobilevideo321.bean.MediaItem;
import com.example.pgg.mobilevideo321.constant.Constant;
import com.example.pgg.mobilevideo321.utils.SPUtils;

import java.io.IOException;
import java.util.ArrayList;



/**
 * Created by pgg on 18-6-14.
 */

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {


    public static final String OPENAUDIO = "com.pgg.mobilevideo._OPENAUDIO";
    private ArrayList<MediaItem> mediaItems;
    private int position;
    private MediaItem mediaItem;
    private MediaPlayer mediaPlayer;
    private NotificationManager manager;

    //顺序播放
    public static final int REPEAT_NORMAL=1;

    //单曲循环
    public static final int REPEAT_SINGLE=2;

    //列表循环
    public static final int REPEAT_ALL=3;

    private int play_mode=REPEAT_NORMAL;


    @Override
    public void onCreate() {
        super.onCreate();
        play_mode= (int) SPUtils.get(this,Constant.PLAY_MODE,0);
        //加载音乐列表
        getDataFromLocal();
    }

    private void getDataFromLocal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = getContentResolver();
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
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent==null){
            manager.cancelAll();
        }
        return stub;
    }

    private IMusicPlayService.Stub stub=new IMusicPlayService.Stub() {
        MusicPlayerService service=MusicPlayerService.this;
        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            service.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {

            return service.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }
    };

    private void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    private boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * 根据位置打开音频文件
     * @param position
     */
    private void openAudio(int position){
        this.position=position;
        if (mediaItems!=null&&mediaItems.size()>0){
            mediaItem = mediaItems.get(position);
            if (mediaPlayer!=null){
                mediaPlayer.reset();
            }
            mediaPlayer=new MediaPlayer();
            try {
                mediaPlayer.setDataSource(mediaItem.getData());
                //设置监听
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);
                mediaPlayer.prepareAsync();

                if (play_mode==MusicPlayerService.REPEAT_SINGLE){
                    //单曲循环
                    mediaPlayer.setLooping(true);
                }else{
                    mediaPlayer.setLooping(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this,"无数据...",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 播放音乐
     */
    private void start(){
        mediaPlayer.start();
        manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent=new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("Notification",true);//标识从状态栏
        PendingIntent pendingIntent=PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification no = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("321音乐")
                .setContentText("正在播放"+getName())
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1,no);
    }

    /**
     * 暂停
     */
    private void pause(){
        mediaPlayer.pause();
        manager.cancel(1);
    }

    /**
     * 停止
     */
    private void stop(){
        mediaPlayer.stop();
        manager.cancel(1);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        manager.cancelAll();
        super.onTaskRemoved(rootIntent);
    }

    /**
     * 获取当前播放进度
     * @return
     */
    private int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    private int getDuration(){
        return  mediaPlayer.getDuration();
    }

    /**
     * 获取演唱者名称
     * @return
     */
    private String getArtist(){
        return mediaItem.getArtist();
    }

    /**
     * 获取歌曲名称
     * @return
     */
    private String getName(){
        return mediaItem.getName();
    }


    /**
     * 获取歌曲路径
     * @return
     */
    private String getAudioPath(){
        return mediaItem.getData();
    }

    /**
     * 播放下一个音乐
     */
    private void next(){
        //1.根据当前的播放模式，设置下一个位置
        setNextPosition();
        //2.根据当前的播放模式和下表位置播放音频
        openNextAudio();
    }

    private void openNextAudio() {
        int playmode = getPlayMode();
        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            if(position < mediaItems.size()){
                //正常范围
                openAudio(position);
            }else{
                position = mediaItems.size()-1;
            }
        }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);
        }else if(playmode ==MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        }else{
            if(position < mediaItems.size()){
                //正常范围
                openAudio(position);
            }else{
                position = mediaItems.size()-1;
            }
        }
    }

    private void setNextPosition() {
        int playmode = getPlayMode();
        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            position++;
        }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
            position++;
            if(position >=mediaItems.size()){
                position = 0;
            }
        }else if(playmode ==MusicPlayerService.REPEAT_ALL){
            position++;
            if(position >=mediaItems.size()){
                position = 0;
            }
        }else{
            position++;
        }
    }

    private void pre(){
        //1.根据当前的播放模式，设置Shang一个位置
        setPrePosition();
        //2.根据当前的播放模式和下表位置播放音频
        openPreAudio();
    }

    private void openPreAudio() {
        int playmode = getPlayMode();
        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            if(position >= 0){
                //正常范围
                openAudio(position);
            }else{
                position = 0;
            }
        }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);
        }else if(playmode ==MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        }else{
            if(position >= 0){
                //正常范围
                openAudio(position);
            }else{
                position = 0;
            }
        }
    }

    private void setPrePosition() {
        int playmode = getPlayMode();
        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            position--;
        }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
            position--;
            if(position < 0){
                position = mediaItems.size()-1;
            }
        }else if(playmode ==MusicPlayerService.REPEAT_ALL){
            position--;
            if(position < 0){
                position = mediaItems.size()-1;
            }
        }else{
            position--;
        }
    }

    /**
     * 设置播放模式
     */
    private void setPlayMode(int playMode){
        this.play_mode=playMode;
        SPUtils.put(this, Constant.PLAY_MODE,playMode);

        if (play_mode==MusicPlayerService.REPEAT_SINGLE){
            //单曲循环
            mediaPlayer.setLooping(true);
        }else{
            mediaPlayer.setLooping(false);
        }
    }

    /**
     * 得到播放模式
     * @return
     */
    private int getPlayMode(){
        return play_mode;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        //通知Activity获取信息,通过广播
        notifyChange(OPENAUDIO);
        start();
    }

    /**
     * 根据action发送广播
     * @param openaudio
     */
    private void notifyChange(String openaudio) {
        Intent intent=new Intent(openaudio);
        sendBroadcast(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        next();
        return true;
    }
}
