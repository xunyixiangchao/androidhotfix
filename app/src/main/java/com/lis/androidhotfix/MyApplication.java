package com.lis.androidhotfix;

import android.app.Application;

import com.lis.hotfix.Hotfix;

import java.io.File;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Hotfix.installPatch(this,new File("/data/data/com.lis.androidhotfix/patch.jar"));
        Hotfix.installPatch(this,new File("/data/user/0/com.lis.androidhotfix/patch.jar"));

    }
}
