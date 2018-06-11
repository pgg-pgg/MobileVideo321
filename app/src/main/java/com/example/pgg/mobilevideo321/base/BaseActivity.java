package com.example.pgg.mobilevideo321.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;

/**
 * Created by pgg on 18-6-11.
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {
    private ProgressDialog mProgressDialog;
    FragmentManager fragmentManager;

    /**
     * 初始化布局
     */
    public abstract void initContentView();

    /**
     * 初始化控件
     * @param savedInstanceState
     */
    public abstract void initView(Bundle savedInstanceState);

    /**
     * 初始化控制中心
     */
    public abstract void initPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initContentView();

        //初始化View注入
        ButterKnife.bind(this);

        initPresenter();
        initView(savedInstanceState);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void showProgress(boolean flag, String message) {
        if (mProgressDialog==null){
            mProgressDialog=new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(flag);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage(message);
        }
        mProgressDialog.show();
    }

    @Override
    public void showProgress(String message) {
        showProgress(true,message);
    }

    @Override
    public void showProgress() {
        showProgress(true);
    }

    @Override
    public void showProgress(boolean flag) {
        showProgress(true,"");
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog==null){
            return;
        }
        if (mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showToast(int resId) {
        showToast(getString(resId));
    }

    @Override
    public void showToast(String msg) {
        if (!isFinishing()){
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void close() {
        finish();
    }

    //--------------------------Fragment相关--------------------------//

    /**
     * 获取Fragment的管理器
     * @return
     */
    public FragmentManager getBaseFragmentManager(){
        if (fragmentManager==null){
            fragmentManager=getSupportFragmentManager();
        }
        return fragmentManager;
    }

    /**
     * 获取Fragment事务管理
     * @return
     */
    public FragmentTransaction getFragmentTransaction(){
        return getBaseFragmentManager().beginTransaction();
    }

    /**
     * 替换一个Fragment
     * @param res
     * @param fragment
     */
    public void replaceFragment(int res,BaseFragment fragment){
        replaceFragment(res,fragment,false);
    }

    /**
     * 替换一个Fragment并设置是否加入回退栈
     * @param res
     * @param fragment
     * @param isAddToBackStack
     */
    private void replaceFragment(int res, BaseFragment fragment, boolean isAddToBackStack) {
        FragmentTransaction fragmentTransaction=getFragmentTransaction();
        fragmentTransaction.replace(res,fragment);
        if (isAddToBackStack){
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    /**
     * 添加一个Fragment
     * @param res
     * @param fragment
     */
    public void addFragment(int res, Fragment fragment){
        FragmentTransaction fragmentTransaction=getFragmentTransaction();
        fragmentTransaction.add(res,fragment);
        fragmentTransaction.commit();
    }

    public void addFragment(int res, Fragment fragment,String tag){
        FragmentTransaction fragmentTransaction=getFragmentTransaction();
        fragmentTransaction.add(res,fragment,tag);
        fragmentTransaction.commit();
    }

    /**
     * 移除一个Fragment
     * @param fragment
     */
    public void removeFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getFragmentTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    public void showFragment(Fragment fragment){
        Logger.e("状态显示");
        if (fragment.isHidden()){
            Logger.e("恢复状态Fragment");
            FragmentTransaction fragmentTransaction=getFragmentTransaction();
            fragmentTransaction.show(fragment);
            fragmentTransaction.commit();
        }
    }

    public void hideFragment(Fragment fragment){
        if (!fragment.isHidden()){
            FragmentTransaction fragmentTransaction=getFragmentTransaction();
            fragmentTransaction.hide(fragment);
            fragmentTransaction.commit();
        }
    }

    //--------------------------Fragment相关end--------------------------//




}
