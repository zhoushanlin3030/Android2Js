package com.example.zsl.bluetest;

import android.webkit.JavascriptInterface;

/**
 * Routing
 * Desc TODO
 * Source
 * Created by zsl on 2019/3/23 17:39.
 * Modify by zsl on 2019/3/23 17:39.
 * Version 1.0
 */
public class AndroidtoJs {

    @JavascriptInterface
    public String hello(String msg) {
        return "Android:"+msg;
    }
}
