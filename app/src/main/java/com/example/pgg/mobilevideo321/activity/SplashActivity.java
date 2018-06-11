package com.example.pgg.mobilevideo321.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.constant.Constant;
import com.example.pgg.mobilevideo321.presenter.SplashPresenter;
import com.example.pgg.mobilevideo321.utils.SPUtils;
import com.example.pgg.mobilevideo321.utils.StateBarTranslucentUtils;
import com.example.pgg.mobilevideo321.view.SplashView;
import com.flaviofaria.kenburnsview.KenBurnsView;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by pgg on 18-6-11.
 */

@RuntimePermissions
public class SplashActivity extends AppCompatActivity implements SplashView{
    public KenBurnsView mKenBurns;

    public ImageView mLogo;

    public TextView welcomeText;

    private SplashPresenter mPresenter;

    Animation anim;
    ObjectAnimator alphaAnimation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        mPresenter=new SplashPresenterImpl(this);

        boolean isFirstOpen= (boolean) SPUtils.get(this, Constant.FIRST_OPEN,false);
        mPresenter.isFirstOpen(isFirstOpen);
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_splash);

        mKenBurns =  findViewById(R.id.ken_burns_images);
        mLogo =  findViewById(R.id.logo_splash);
        welcomeText =  findViewById(R.id.welcome_text);
        //设置状态栏透明
        StateBarTranslucentUtils.setStateBarTranslucent(this);
        Glide.with(this)
                .load(R.drawable.welcometoqbox)
                .into(mKenBurns);

        animator1();
        animator2();
    }

    private void animator2() {
        alphaAnimation=ObjectAnimator.ofFloat(welcomeText,"alpha",0.0f,1.0f);
        alphaAnimation.setStartDelay(1700);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
    }

    private void animator1() {
        mLogo.setAlpha(1.0f);
        anim= AnimationUtils.loadAnimation(this,R.anim.translate_top_to_center);
        mLogo.startAnimation(anim);
    }



    @Override
    @NeedsPermission({Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE})
    public void startWelcomeGuideActivity() {
        WelcomeGuideActivity.start(this);
        finish();
    }

    /**
     * 为什么需要这个权限，给以用户说明
     */
    @OnShowRationale({Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE})

    void showRationaleForWrite(final PermissionRequest request){
        new AlertDialog.Builder(this).setMessage("有部分权限需要你的授权").setPositiveButton(R.string.imtrue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                request.proceed();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                request.cancel();
            }
        }).create().show();
    }

    /**
     * 如果用户不授予权限调用的方法
     */
    @OnPermissionDenied({Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            /*Manifest.permission.WRITE_CONTACTS,*/
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForCamera() {
        showPermissionDenied();
    }

    /**
     * 如果用户拒绝授予权限
     */
    public void showPermissionDenied(){
        new AlertDialog.Builder(this)
                .setTitle("权限说明")
                .setCancelable(false)
                .setMessage("本应用需要部分必要的权限，如果不授予可能会影响正常使用！")
                .setNegativeButton("退出应用",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("赋予权限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SplashActivityPermissionsDispatcher.startWelcomeGuideActivityWithCheck(SplashActivity.this);
                    }
                })
                .create().show();
    }

    /**
     * 如果用户选择了让设备“不再询问”，而调用的方法
     */
    @OnNeverAskAgain({Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            /*Manifest.permission.WRITE_CONTACTS,*/
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForCamera() {
        Toast.makeText(this, "不再询问授权权限！请手动在系统设置权限", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void startHomeActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mKenBurns != null) {
            mKenBurns.pause();
        }
        if (alphaAnimation != null) {
            alphaAnimation.cancel();
        }
        if (anim != null) {
            anim.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mKenBurns != null) {
            mKenBurns.resume();
        }
    }
}
