package com.example.zsl.bluetest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Set;

public class MainActivity extends Activity implements View.OnClickListener {

    WebView     mWebview;
    WebSettings mWebSettings;
    TextView    beginLoading,endLoading,loading,mtitle;

    @SuppressLint ("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_Jtf).setOnClickListener(this);
        mWebview = (WebView) findViewById(R.id.webView1);
        beginLoading = (TextView) findViewById(R.id.text_beginLoading);
        endLoading = (TextView) findViewById(R.id.text_endLoading);
        loading = (TextView) findViewById(R.id.text_Loading);
        mtitle = (TextView) findViewById(R.id.title);
        mWebSettings = mWebview.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        mWebview.addJavascriptInterface(new AndroidtoJs(), "test");

        mWebview.loadUrl("file:////android_asset/test3.html");        //设置不用系统浏览器打开,直接显示在当前Webview

        //设置WebChromeClient类
        mWebview.setWebChromeClient(new WebChromeClient() {            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                System.out.println("标题在这里");
                mtitle.setText(title);
            }            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    String progress = newProgress + "%";
                    loading.setText(progress);
                } else if (newProgress == 100) {
                    String progress = newProgress + "%";
                    loading.setText(progress);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Alert");
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok,
                                          new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialogInterface,
                                                                  int i) {
                                                result.confirm();
                                              }
                                          });
                builder.setCancelable(false);
                builder.create().show();
                return true;
            }
        });

        //设置WebViewClient类
        mWebview.setWebViewClient(new WebViewClient() {            //设置加载前的函数
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
//                js://webview?arg1=111&arg2=222
                if (uri.getScheme().equals("js")) {
                    if (uri.getAuthority().equals("webview")) {
                        Toast.makeText(MainActivity.this, "拦截url方式js调用Android", Toast.LENGTH_SHORT).show();
                        HashMap<String,String> map = new HashMap<>();
                        Set<String> names = uri.getQueryParameterNames();
                        for (String name:names) {
                            map.put(name,uri.getQueryParameter(name));
                        }
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view,url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                System.out.println("开始加载了");
                beginLoading.setText("开始加载了");
            }            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                endLoading.setText("结束加载了");
            }
        });
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
//        mWebview.post(new Runnable() {
//            @Override
//            public void run() {
//                mWebview.loadUrl("javaScript:callJS()");
//            }
//        });

        mWebview.evaluateJavascript("javaScript:callJS()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {

                Log.d("TAG","result-"+s);
            }
        });

    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();
            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        super.onDestroy();
    }

}
