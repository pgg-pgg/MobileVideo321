package com.example.pgg.mobilevideo321.widget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.activity.SearchActivity;

/**
 * Created by pgg on 18-6-11.
 * 自定义标题栏
 */

public class TitleBar extends LinearLayout implements View.OnClickListener {

    private View tv_search;
    private View rl_game;
    private View iv_record;
    private Context context;

    public TitleBar(Context context) {
        this(context,null);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    /**
     * 当布局文件加载完成回调此方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到孩子实例
        tv_search=getChildAt(1);
        rl_game=getChildAt(2);
        iv_record=getChildAt(3);
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search:
                //搜索
                Intent intent=new Intent(context,SearchActivity.class);
                context.startActivity(intent);
                break;
            case R.id.rl_game:
                Toast.makeText(context,"游戏",Toast.LENGTH_LONG).show();
                break;
            case R.id.iv_record:
                Toast.makeText(context,"历史",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
