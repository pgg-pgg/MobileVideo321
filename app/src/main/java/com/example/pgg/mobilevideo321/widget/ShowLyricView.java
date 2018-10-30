package com.example.pgg.mobilevideo321.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


import com.example.pgg.mobilevideo321.bean.Lyric;
import com.example.pgg.mobilevideo321.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by pgg on 18-6-15.
 * 显示歌词控件
 */

public class ShowLyricView extends AppCompatTextView {

    ArrayList<Lyric> lyrics;
    Paint paint;
    int width;
    int height;
    //歌词列表中的索引
    private int index;
    private float textHeight;
    int currentPosition;

    //高亮显示歌词的时间
    float sleepTime;

    float timePoint;

    public void setLyrics(ArrayList<Lyric> lyrics){
        this.lyrics=lyrics;
    }

    public ShowLyricView(Context context) {
        this(context,null);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=w;
        height=h;
    }

    private void initView(Context context) {
        textHeight= DensityUtil.dip2px(context,20);
        paint=new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(DensityUtil.dip2px(context,20));
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics!=null&&lyrics.size()>0){

            //往上推移
            float push=0;
            if (sleepTime==0){
                push=0;
            }else {
                //平移
                float delta=((currentPosition-timePoint)/sleepTime)*textHeight;
                push=textHeight+delta;
            }
            canvas.translate(0,-push);



            //绘制当前句
            String currentText = lyrics.get(index).getContent();
            paint.setColor(Color.GREEN);
            canvas.drawText(currentText,width/2,height/2,paint);
            //绘制之前句
            float tempY=height/2;//y轴中间坐标
            for(int i=index-1;i>=0;i--){
                //得到每一句歌词
                String preContent=lyrics.get(i).getContent();
                tempY=tempY-textHeight;
                if (tempY<0){
                    break;
                }
                paint.setColor(Color.WHITE);
                canvas.drawText(preContent,width/2,tempY,paint);
            }
            //绘制之后句
            tempY=height/2;//y轴中间坐标
            for(int i=index+1;i<lyrics.size();i++){
                //得到每一句歌词
                String nextContent=lyrics.get(i).getContent();
                tempY=tempY+textHeight;
                if (tempY>height){
                    break;
                }
                paint.setColor(Color.WHITE);
                canvas.drawText(nextContent,width/2,tempY,paint);
            }
        }else {
            paint.setColor(Color.GREEN);
            canvas.drawText("没有歌词",width/2,height/2,paint);
        }
    }

    //根据当前播放位置，找出该高亮显示的歌词
    public void setShowNextLyric(int currentPosition) {
        this.currentPosition=currentPosition;
        if (lyrics==null||lyrics.size()==0){
            return;
        }

        for (int i=1;i<lyrics.size();i++){
            if (currentPosition<lyrics.get(i).getTimePoint()){
                int tempIndex=i-1;
                if (currentPosition>=lyrics.get(tempIndex).getTimePoint()){
                    //当前正在播放的歌词
                    index=tempIndex;
                    sleepTime=lyrics.get(index).getSleepTime();
                    timePoint=lyrics.get(index).getTimePoint();

                }
            }
        }

        //重绘
        invalidate();
    }
}
