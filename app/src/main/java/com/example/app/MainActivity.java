package com.wekanapp.app;

import android.os.Build;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.webkit.ConsoleMessage;


import android.net.http.SslError;
import android.webkit.SslErrorHandler;


public class MainActivity extends Activity {

    //private WebView mWebView;
    private static final String TAG = MainActivity.class.getSimpleName();
    WebView webview;

    String url = "https://www.baidu.com/";  //云端

    @Override
    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(R.id.activity_main_webview);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                Log.e("tag"," SslError="+error);
                //证书信任
                handler.proceed();
            }
        });

        setUpWebViewDefaults(webview);


        WebSettings settings = webview.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//支持js
        settings.setJavaScriptEnabled(true);

        settings.setUserAgentString("Mozilla/5.0 (android; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.109 Safari/537.36");
        //settings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
//自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
//自动缩放
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setDomStorageEnabled(true);
        settings.setBlockNetworkImage(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
//支持获取手势焦点
        webview.requestFocusFromTouch();
        // REMOTE RESOURCE
        //mWebView.loadUrl("https://github.com/");
        //mWebView.loadUrl(url);

        webview.setWebChromeClient(new WebChromeClient(){

            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {  //在Logcat中显示浏览器的控制台输出
                Log.d("MyApplication", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId() );
                return true;
            }

           // @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            //解決打不开webrtc摄像头的问题；有一点十分重要需要手动在安卓权限中允许摄像头权限
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); // 根据传入的参数再去加载新的网页
                return true; // 表示当前WebView可以处理打开新网页的请求，不用借助系统浏览器
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:function ganen(){ document.getElementsByTagName('meta')['viewport'].content='width=1000px,initial-scale=0.5,minimum-scale=0.2;'}");
                view.loadUrl("javascript:ganen();");
            }

        });


        webview.setWebViewClient(new WebViewClient() {  //解決https 不安全的显示问题
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                //handler.cancel();// super中默认的处理方式，WebView变成空白页
                if (handler != null) {
                    handler.proceed();//忽略证书
                }
            }
        });

        webview.getSettings().setMediaPlaybackRequiresUserGesture(false); //让摄像头自动播放
        webview.loadUrl(url);

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                Log.d(TAG, "onPermissionRequest");
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(request.getOrigin().toString().equals(url)) {
                            request.grant(request.getResources());
                        } else {
                            request.deny();
                        }
                    }
                });
            }

        });

        //mWebView.loadUrl("https://www.cncwww.cn:9944/call.html");
        // LOCAL RESOURCE
        // mWebView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        if(webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

        // Allow use of Local Storage
        settings.setDomStorageEnabled(true);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.setWebViewClient(new WebViewClient());

        // AppRTC requires third party cookies to work
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(webview, true);
    }
}
