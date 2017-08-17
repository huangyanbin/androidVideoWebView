package com.example.huang.videoview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.huang.videoview.webview.MeWebView;
import com.example.huang.videoview.webview.WebHelper;
import com.example.huang.videoview.webview.WebViewCallback;

public class MainActivity extends AppCompatActivity implements WebViewCallback,View.OnClickListener{
    private MeWebView webView;
    private Button mBtn;
    private EditText mEt;
    private Button mOpenBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (MeWebView) findViewById(R.id.webView);
        mBtn = (Button)findViewById(R.id.button);
        mOpenBtn = (Button)findViewById(R.id.button2);
        mEt = (EditText)findViewById(R.id.et);
        mBtn.setOnClickListener(this);

        mOpenBtn.setOnClickListener(this);
        webView.setWebHelper(new WebHelper());
        webView.setWebViewCallback(this);
        webView.loadUrl("http://193.28.20.62:8889/artVideo");
    }

    @Override
    public void onPageStart(String url) {
        mEt.setText(url);
    }

    @Override
    public void onPageEnd() {

    }

    @Override
    public void onPageProgress(int progress) {

    }

    @Override
    public void onLoadUrl(String url) {

    }

    @Override
    public void onReceivedTitle(String title) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button) {
            webView.loadUrl(mEt.getText().toString());
        }else {
            Intent i = new Intent(this,EswVideoActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { //表示按返回键 时的操作
                // 监听到返回按钮点击事件
                if(webView.goWebBack()){
                    return true;
                }
            }
        }
        return  super.onKeyDown(keyCode,event);
    }
}
