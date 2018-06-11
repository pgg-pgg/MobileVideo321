package com.example.pgg.mobilevideo321.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pgg.mobilevideo321.R;


/**
 * Created by pgg on 2018/5/3.
 */

public class TabBar_Main extends LinearLayout {

    private String mName;
    private Drawable mIcon;
    private ImageView sIconImgView;
    private TextView sNameTv;

    public TabBar_Main(Context context) {
        super(context,null);
    }

    public TabBar_Main(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.tabbar_main,this,true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabBar_Attr);
        mName = typedArray.getString(R.styleable.TabBar_Attr_name);
        mIcon = typedArray.getDrawable(R.styleable.TabBar_Attr_icon);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        sIconImgView = findViewById(R.id.icon_tabbar);
        sNameTv = findViewById(R.id.name_tabbar);
        if (TextUtils.isEmpty(mName)) ;
        else setName(mName);
        if (mIcon != null) {
            setIcon(mIcon);
        }
    }

    public void setName(String name) {
        sNameTv.setText(name);
    }

    public void setIcon(Drawable icon) {
        sIconImgView.setImageDrawable(icon);
    }
}
