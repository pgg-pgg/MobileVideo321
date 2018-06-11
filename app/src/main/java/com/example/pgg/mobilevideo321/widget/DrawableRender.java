package com.example.pgg.mobilevideo321.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatDrawableManager;

import com.cleveroad.slidingtutorial.Renderer;
import com.example.pgg.mobilevideo321.R;
import com.orhanobut.logger.Logger;

/**
 * Created by pgg on 2018/5/3.
 */

@SuppressWarnings("WeakerAccess")
public class DrawableRender implements Renderer {

    private Drawable mActiveDrawable;
    private Drawable mDrawable;

    public static DrawableRender create(@NonNull Context context){
        return new DrawableRender(context);
    }

    @SuppressLint("RestrictedApi")
    public DrawableRender(Context context) {
        try {
            mActiveDrawable= AppCompatDrawableManager.get().getDrawable(context, R.drawable.vec_checkbox_fill_circle_outline);
            mDrawable= AppCompatDrawableManager.get().getDrawable(context,R.drawable.vec_checkbox_blank_circle_outline);
        }catch (Resources.NotFoundException notFoundException){
            Logger.e(notFoundException.getMessage());
        }catch (Exception e){
            Logger.e(e.getMessage());
        }finally {
            mActiveDrawable=context.getResources().getDrawable(R.drawable.vec_checkbox_fill_circle_outline);
            mDrawable=context.getResources().getDrawable(R.drawable.vec_checkbox_empty_circle);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull RectF elementBounds, @NonNull Paint paint, boolean isActive) {
        Drawable dr=isActive?mActiveDrawable:mDrawable;
        dr.setBounds((int)elementBounds.left,(int)elementBounds.top,(int)elementBounds.right,
                (int)elementBounds.bottom);
        dr.draw(canvas);
    }
}
