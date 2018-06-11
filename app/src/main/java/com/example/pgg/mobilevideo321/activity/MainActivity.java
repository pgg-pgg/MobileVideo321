package com.example.pgg.mobilevideo321.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.base.BaseActivity;
import com.example.pgg.mobilevideo321.constant.Constant;
import com.example.pgg.mobilevideo321.fragment.main.AudioFragment;
import com.example.pgg.mobilevideo321.fragment.main.NetAudioFragment;
import com.example.pgg.mobilevideo321.fragment.main.NetVideoFragment;
import com.example.pgg.mobilevideo321.fragment.main.VideoFragment;
import com.example.pgg.mobilevideo321.global.MyApplication;
import com.example.pgg.mobilevideo321.utils.StatusBarCompat;
import com.example.pgg.mobilevideo321.widget.TabBar_Main;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    public static List<String> logList=new CopyOnWriteArrayList<>();


    private static final String VIDEO_FRAGMENT="VIDEO_FRAGMENT";
    private static final String AUDIO_FRAGMENT = "AUDIO_FRAGMENT";
    private static final String NET_VIDEO_FRAGMENT = "NET_VIDEO_FRAGMENT";
    public static final String NET_AUDIO_FRAGMENT = "NET_AUDIO_FRAGMENT";

    @BindView(R.id.fl_main_content)
    FrameLayout fl_main_content;

    @BindView(R.id.rb_video)
    TabBar_Main rb_video;
    @BindView(R.id.rb_audio)
    TabBar_Main rb_audio;
    @BindView(R.id.rb_net_video)
    TabBar_Main rb_net_video;
    @BindView(R.id.rb_net_audio)
    TabBar_Main rb_net_audio;

    public VideoFragment videoFragment;
    public AudioFragment audioFragment;
    public NetVideoFragment netVideoFragment;
    public NetAudioFragment netAudioFragment;

    private FragmentManager sBaseFragmentManager;

    boolean isRestart=false;

    /**
     * 存储当前Fragment的标记
     */
    private String mCurrentIndex;
    private boolean mIsExit;

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_main);
        MyApplication.setMainActivity(this);
        StatusBarCompat.compat(this);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        sBaseFragmentManager=getBaseFragmentManager();
        String startPage=VIDEO_FRAGMENT;
        if (savedInstanceState!=null){
            initByRestart(savedInstanceState);
        }else {
            switchToFragment(startPage);
            mCurrentIndex=startPage;
        }
    }

    @Override
    public void initPresenter() {

    }

    private void switchToFragment(String index) {
        hideAllFragment();
        switch (index){
            case VIDEO_FRAGMENT:
                if (rb_video.getVisibility()== View.VISIBLE){
                    showVideoFragment();
                    Logger.e("newsopen:101");
                }
                break;
            case AUDIO_FRAGMENT:
                showAudioFragment();
                break;
            case NET_VIDEO_FRAGMENT:
                showNetVideoFragment();
                break;
            case NET_AUDIO_FRAGMENT:
                showNetAudioFragment();
                break;
            default:
                break;
        }
        mCurrentIndex=index;
    }

    private void showNetAudioFragment() {
        if (false==rb_net_audio.isSelected()){
            rb_net_audio.setSelected(true);
        }
        if (netAudioFragment==null){
            netAudioFragment=NetAudioFragment.newInstance(this);
            addFragment(R.id.fl_main_content,netAudioFragment,NET_AUDIO_FRAGMENT);
        }else if (isRestart==true){
            getFragmentTransaction().show(netAudioFragment).commit();
            isRestart=false;
        }else {
            showFragment(netAudioFragment);
        }
    }

    private void showNetVideoFragment() {
        if (false==rb_net_video.isSelected()){
            rb_net_video.setSelected(true);
        }
        if (netVideoFragment==null){
            netVideoFragment=NetVideoFragment.newInstance(this);
            addFragment(R.id.fl_main_content,netVideoFragment,NET_VIDEO_FRAGMENT);
        }else if (isRestart=true){
            isRestart=false;
            getFragmentTransaction().show(netVideoFragment).commit();
        }else {
            showFragment(netVideoFragment);
        }
    }

    private void showAudioFragment() {
        if (false==rb_audio.isSelected()){
            rb_audio.setSelected(true);
        }
        if (audioFragment==null){
            audioFragment=AudioFragment.newInstance(this);
            addFragment(R.id.fl_main_content,audioFragment,AUDIO_FRAGMENT);
        }else if (isRestart=true){
            isRestart=false;
            getFragmentTransaction().show(audioFragment).commit();
        }else {
            showFragment(audioFragment);
        }
    }

    private void showVideoFragment() {
        if (rb_video.getVisibility()!=View.VISIBLE){
            return;
        }
        if (false==rb_video.isSelected()){
            rb_video.setSelected(true);
        }
        if (videoFragment==null){
            Logger.e("恢复状态："+"null");
            videoFragment=new VideoFragment().newInstance(this);
            addFragment(R.id.fl_main_content,videoFragment,VIDEO_FRAGMENT);
        }else if (isRestart=true){
            isRestart=false;
            getFragmentTransaction().show(videoFragment).commit();
        }else {
            showFragment(videoFragment);
        }
    }

    private void hideAllFragment() {
        if (videoFragment!=null){
            hideFragment(videoFragment);
        }
        if (audioFragment!=null){
            hideFragment(audioFragment);
        }
        if (netVideoFragment!=null){
            hideFragment(netVideoFragment);
        }
        if (netAudioFragment!=null){
            hideFragment(netAudioFragment);
        }
        if (rb_video.getVisibility()==View.VISIBLE){
            rb_video.setSelected(false);
        }
        rb_audio.setSelected(false);
        rb_net_video.setSelected(false);
        rb_net_audio.setSelected(false);

    }

    private void initByRestart(Bundle savedInstanceState) {
        mCurrentIndex=savedInstanceState.getString("mCurrentIndex");

        isRestart=true;
        Logger.e("恢复状态"+mCurrentIndex);
        netAudioFragment= (NetAudioFragment) sBaseFragmentManager.findFragmentByTag(NET_AUDIO_FRAGMENT);
        if (rb_video.getVisibility()==View.VISIBLE){
            videoFragment= (VideoFragment) sBaseFragmentManager.findFragmentByTag(VIDEO_FRAGMENT);
        }
        audioFragment= (AudioFragment) sBaseFragmentManager.findFragmentByTag(AUDIO_FRAGMENT);
        netVideoFragment= (NetVideoFragment) sBaseFragmentManager.findFragmentByTag(NET_VIDEO_FRAGMENT);

        switchToFragment(mCurrentIndex);
    }

    @OnClick({R.id.rb_video,R.id.rb_audio,R.id.rb_net_video,R.id.rb_net_audio})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rb_video:
                if (!mCurrentIndex.equals(VIDEO_FRAGMENT)) {
                    switchToFragment(VIDEO_FRAGMENT);
                }
                break;
            case R.id.rb_audio:
                if (!mCurrentIndex.equals(AUDIO_FRAGMENT)){
                    switchToFragment(AUDIO_FRAGMENT);
                }
                break;
            case R.id.rb_net_video:
                if (!mCurrentIndex.equals(NET_VIDEO_FRAGMENT)){
                    switchToFragment(NET_VIDEO_FRAGMENT);
                }
                break;
            case R.id.rb_net_audio:
                if (!mCurrentIndex.equals(NET_AUDIO_FRAGMENT)){
                    switchToFragment(NET_AUDIO_FRAGMENT);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Constant.VIDEOFRAGMENT_CATEGORYACTIVITY_REQUESTCODE &&resultCode==Constant.VIDEOFRAGMENT_CATEGORYACTIVITY_RESULTCODE){
            videoFragment.initView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //b EventBus.getDefault().unregister(this);
        for (Fragment fragment:getBaseFragmentManager().getFragments()){
            getFragmentTransaction().remove(fragment);
        }
        MyApplication.setMainActivity(null);
        //todo 解决Android输入法造成的内存泄漏问题
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLogInfo();
    }

    private void refreshLogInfo() {
        String AllLog="";
        for (String log:logList){
            AllLog=AllLog+log+"\n\n";
        }
        Logger.e(AllLog);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mCurrentIndex",mCurrentIndex);
        Logger.e("保存状态");
    }

    /**
     * 监听手机的返回键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if (mIsExit){
                this.finish();
            }else {
                Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
                mIsExit=true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit=false;
                    }
                },2000);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 优雅的退出程序，当有其他地方退出应用时，会先返回到此页面在执行退出
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent!=null){
            boolean isExit=intent.getBooleanExtra(Constant.TAG_EXIT,false);
            if (isExit){
                finish();
            }
        }
    }
}
