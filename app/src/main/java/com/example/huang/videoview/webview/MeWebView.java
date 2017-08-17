package com.example.huang.videoview.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;



public class MeWebView extends WebView  {

    private WebViewCallback callback;
    private boolean isPauseVideo; // webView onPause 是否暂停了视频播放
    private boolean isError;
    private WebHelper mWebHelper;

    @SuppressLint("SetJavaScriptEnabled")
    public MeWebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);

        initWebViewSettings();
        setWebChromeClient(new CustomWebViewChromeClient());
        setWebViewClient(new CustomWebClient());
        addJavascriptInterface(new JsObject(), "onClick");

    }

    public WebHelper getWebHelper() {
        return mWebHelper;
    }

    public void setWebHelper(WebHelper webHelper) {
        this.mWebHelper = webHelper;

    }

    @Override
    public void loadUrl(String s) {
        if(this.callback !=null){
            callback.onLoadUrl(s);
        }
        super.loadUrl(s);
    }

    private class JsObject {


        @JavascriptInterface
        public void videoPause(){
            //activity暂停时是否Video也暂停了
            isPauseVideo = true;
        }

    }

    private class CustomWebViewChromeClient extends WebChromeClient {

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
          //  fullScreen();
            if(mWebHelper != null && getContext() != null){
               mWebHelper.showContainerView(MeWebView.this,(Activity) getContext(),view);
                // 横屏显示
                ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                // 设置全屏
                mWebHelper.setFullScreen((Activity) getContext());
            }


            super.onShowCustomView(view, callback);
        }

        @Override
        public void onProgressChanged(WebView webView, int i) {
            super.onProgressChanged(webView, i);
            if (callback != null) {
                callback.onPageProgress(i);
            }
        }

        @Override
        public void onReceivedTitle(WebView arg0, final String title) {
            super.onReceivedTitle(arg0, title);
            if (callback != null) {
                callback.onReceivedTitle(title);
            }

        }


        @Override
        public void onHideCustomView() {
           fullScreen();
            if(mWebHelper != null && getContext() != null){
                ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mWebHelper.hideContainerView((Activity) getContext());
                // 横屏显示
                // 设置全屏
                mWebHelper.quitFullScreen((Activity) getContext());
            }
            super.onHideCustomView();
        }
    }

    private void fullScreen() {
       /* if(mWebHelper != null&& getContext() != null){
           mWebHelper.fullScreen((Activity) getContext());
        }*/
        /*String fullScreenJs = TagUtils.onFullScreenJs(getUrl());
        if( fullScreenJs !=null){
            loadUrl(fullScreenJs);
        }*/
    }



    private class CustomWebClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
                String js = WebHelper.getPlayVideoJS();
                view.loadUrl(js);

            if (callback != null) {
                callback.onPageEnd();
            }
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            loadUrl("file:///android_asset/404.html");
            isError = true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (!url.startsWith("http") && !url.startsWith("javascript")); //暂时屏蔽掉打开外部请求
        }


        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            if (callback != null) {
                callback.onPageStart(s);
            }
        }
    }


    private void initWebViewSettings() {
        WebSettings ws = getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        ws.setPluginState(WebSettings.PluginState.ON);
        ws.setRenderPriority(WebSettings.RenderPriority.HIGH);
        ws.setLoadWithOverviewMode(true);
        ws.setBuiltInZoomControls(true);// 隐藏缩放按钮
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
        ws.setUseWideViewPort(true);// 可任意比例缩放
        ws.setSavePassword(true);
        ws.setSaveFormData(true);// 保存表单数据
        ws.setDomStorageEnabled(true);
        setSaveEnabled(true);
        ws.setSupportZoom(false);
        ws.setAppCacheMaxSize(1024 * 1024 * 8);
        ws.setAllowFileAccess(true);
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setGeolocationEnabled(true);
        ws.setDatabaseEnabled(true);
        setAcceptThirdPartyCookies();

    }

    /**
     * 设置跨域cookie读取
     */
    public final void setAcceptThirdPartyCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }
    }



    public void onDestroy() {
        stopLoading();
        loadUrl(WebHelper.getPauseAudioJS());
        if(mWebHelper != null){
            mWebHelper.cancel();
        }
        try {
            if(getParent() != null){
                ((ViewGroup) getParent()).removeView(this);
            }
            destroy();
        }catch (Exception ignored){

        }
    }

    public boolean goWebBack() {
        if (!isError && canGoBack()) {
            goBack();
            return true;
        }
        return false;
    }




    @Override
    public boolean canGoBack() {
        return !isError && super.canGoBack();
    }

    @Override
    public void onPause() {
        loadUrl(WebHelper.getPauseVideoJS());
        super.onPause();
    }

    @Override
    public void onResume() {
        if(isPauseVideo) {
            loadUrl(WebHelper.getPlayVideoJS());
            isPauseVideo = false;
        }
        super.onResume();
    }



    public WebViewCallback getWebViewCallback() {
        return callback;
    }

    public void setWebViewCallback(WebViewCallback webViewCallback) {
        callback = webViewCallback;

    }
}
