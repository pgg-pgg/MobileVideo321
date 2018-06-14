package com.example.pgg.mobilevideo321.activity;

import com.example.pgg.mobilevideo321.presenter.SplashInteractor;
import com.example.pgg.mobilevideo321.presenter.SplashInteractorImpl;
import com.example.pgg.mobilevideo321.presenter.SplashPresenter;
import com.example.pgg.mobilevideo321.view.SplashView;

/**
 * Created by pgg on 2018/5/2.
 */

public class SplashPresenterImpl implements SplashPresenter,SplashInteractor.OnEnterIntoFinishListener {
    public SplashInteractor splashInteractor;
    public SplashView splashView;

    public SplashPresenterImpl(SplashView splashView) {
        this.splashView=splashView;
        splashInteractor=new SplashInteractorImpl();
    }

    @Override
    public void isFirstOpen(boolean isFirstOpen) {
        splashInteractor.enterInto(isFirstOpen,this);
    }

    @Override
    public void onDestroy() {
        splashView=null;
    }

    @Override
    public void isFirstOpen() {
        SplashActivityPermissionsDispatcher.startWelcomeGuideActivityWithCheck((SplashActivity)splashView);
    }

    @Override
    public void isNotFirstOpen() {
        splashView.startHomeActivity();
    }

    @Override
    public void showContentView() {
        splashView.initContentView();
    }
}
