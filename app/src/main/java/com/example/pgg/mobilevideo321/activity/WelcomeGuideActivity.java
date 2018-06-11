package com.example.pgg.mobilevideo321.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.constant.Constant;
import com.example.pgg.mobilevideo321.fragment.CustomPresentationPagerFragment;
import com.example.pgg.mobilevideo321.utils.SPUtils;
import com.example.pgg.mobilevideo321.utils.StateBarTranslucentUtils;

/**
 * Created by pgg on 18-6-11.
 * 欢迎页
 */

public class WelcomeGuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //设置状态栏透明
        StateBarTranslucentUtils.setStateBarTranslucent(this);
        if(savedInstanceState==null){
            //更换Fragment
            replaceTutorialFragment();
        }
    }

    private void replaceTutorialFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_welcome,new CustomPresentationPagerFragment())
                .commit();
    }
    public static void start(Context context) {
        SPUtils.put(context, Constant.FIRST_OPEN,true);
        context.startActivity(new Intent(context,WelcomeGuideActivity.class));
    }
}
