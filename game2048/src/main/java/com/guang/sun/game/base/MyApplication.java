package com.guang.sun.game.base;

import android.app.Application;

/**
 * Created by sunxi on 2015/8/27.
 */


public class MyApplication extends Application {
    public  int getStatusBarHeight() {
        return statusBarHeight;
    }

    public  void setStatusBarHeight(int statusBarHeight) {
        MyApplication.statusBarHeight = statusBarHeight;
    }

    private static int statusBarHeight;

    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
