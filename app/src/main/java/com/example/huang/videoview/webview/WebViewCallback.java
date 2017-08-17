package com.example.huang.videoview.webview;


/**
 * Created by David on 2017/2/23.
 */

public interface WebViewCallback {


    void onPageStart(String url);
    void onPageEnd();
    void onPageProgress(int progress);
    void onLoadUrl(String url);
    void onReceivedTitle(String title);

}
