package com.lis.androidhotfix;

/**
 * Created by lis on 2020/7/22.
 */
public class Utils {
    private static final String TAG = "Utils";
    public static void test() {
//        Log.d(TAG,"哈哈哈哈");
       throw new IllegalStateException("出错啦！！！！！！！！！！！！！！");
    }
}
