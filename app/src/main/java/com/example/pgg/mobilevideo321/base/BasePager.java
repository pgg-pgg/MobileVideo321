package com.example.pgg.mobilevideo321.base;

import android.content.Context;
import android.view.View;

/**
 * Created by pgg on 18-6-11.
 * ViewPage公共类
 */

public abstract class BasePager {
    public final Context context;

    public View root_view;

    public BasePager(Context context){
        this.context=context;
        root_view=initView();
    }


    public abstract View initView();

    public void initData(){

    }
}
