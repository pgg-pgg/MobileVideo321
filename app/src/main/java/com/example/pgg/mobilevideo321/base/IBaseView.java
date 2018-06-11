package com.example.pgg.mobilevideo321.base;

import android.content.Context;

/**
 * Created by pgg on 2018/5/2.
 * 公共View接口
 */

public interface IBaseView {

    /**
     * 显示进度条
     *
     * @param flag 是否可取消
     * @param message 要显示的信息
     */
    void showProgress(boolean flag, String message);

    /**
     * 显示可取消的进度条
     * @param message 要显示的信息
     */
    void showProgress(String message);

    /**
     * 显示可取消的无文字进度条
     */
    void showProgress();

    /**
     * 显示无文字进度条
     * @param flag 是否可取消
     */
    void showProgress(boolean flag);

    /**
     * 隐藏进度条
     */
    void hideProgress();

    /**
     * 根据资源文件id弹出toast
     * @param resId 资源文件id
     */
    void showToast(int resId);

    /**
     * 根据字符串弹出toast
     * @param msg
     */
    void showToast(String msg);

    /**
     * 获取当前上下文对象
     * @return
     */
    Context getContext();

    /**
     * 结束当前页面
     */
    void close();
}
