package com.example.huang.videoview.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;




import com.example.huang.videoview.R;


/**
 * Created by David on 2017/3/31.
 */

public class WebHelper implements View.OnSystemUiVisibilityChangeListener {

    private static final int AUTO_HIDE_DELAY_SECOND = 2;
    private FrameLayout mContainerView;
    private boolean isFullScreen;



    public WebHelper() {

    }



    /**
     * 得到视频全屏播放View
     */
    private FrameLayout getContainerView(Activity activity) {

        if (mContainerView == null) {
            FrameLayout decorView = getDecorView(activity);
            if (decorView != null) {
                decorView.setOnSystemUiVisibilityChangeListener(this);
                FrameLayout container = (FrameLayout) decorView.findViewById(R.id.videoContainer);
                if (container == null) {
                    container = new FrameLayout(activity);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    container.setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
                    container.setId(R.id.videoContainer);
                    container.setLayoutParams(lp);
                    decorView.addView(container);
                }
                mContainerView = container;
            }
        }
        return mContainerView;
    }

    /**
     * 显示WebView播放视频界面
     *
     * @param activity
     * @param videoView
     * @return
     */
    public boolean showContainerView(WebView webView, Activity activity, View videoView) {
        FrameLayout container = getContainerView(activity);
        if (container != null) {
            container.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            videoView.setLayoutParams(lp);
            container.addView(videoView);
           // hideStatusBar(activity);
            isFullScreen = true;
            return true;
        }
        return false;
    }


    /**
     * 隐藏WebView播放视频界面
     *
     * @param activity
     * @return
     */
    public boolean hideContainerView(Activity activity) {

        FrameLayout container = getContainerView(activity);
        if (container != null) {
            isFullScreen = false;
            container.removeAllViews();
            container.setVisibility(View.GONE);
            //showStatusBar(activity);
            return true;
        }
        return false;
    }

/*    *//**
     * 全屏回调
     *
     * @param activity
     *//*
    public void fullScreen(Activity activity) {
        if (activity != null) {
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }*/

    /**
     * 设置全屏
     */
    public void setFullScreen(Activity activity) {
        // 设置全屏的相关属性，获取当前的屏幕状态，然后设置全屏
        if (activity != null) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 退出全屏
     */
    public void quitFullScreen(Activity activity) {
        // 声明当前屏幕状态的参数并获取
        if (activity != null) {
            final WindowManager.LayoutParams attrs =activity.getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attrs);
            activity.getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
    /**
     * 得到Activity根View
     *
     * @param activity
     * @return
     */
    private FrameLayout getDecorView(Activity activity) {

        if (activity != null && activity.getWindow() != null) {
            Window window = activity.getWindow();
            ViewGroup decorView = (ViewGroup) window.getDecorView().findViewById(android.R.id.content);
            if (decorView == null) {
                decorView = (ViewGroup) window.getDecorView();
            }
            if (decorView != null && decorView instanceof FrameLayout) {
                return (FrameLayout) decorView;
            }
        }
        return null;
    }




    public void cancel() {
        //解绑

        isFullScreen = false;
    }

    //状态栏发生变化时
    @Override
    public void onSystemUiVisibilityChange(int visibility) {

        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            if (isFullScreen && mContainerView != null) {
                /*hideStatusBarSub = Observable.timer(AUTO_HIDE_DELAY_SECOND, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                final Activity activity = (Activity) mContainerView.getContext();
                                if (activity != null && !activity.isFinishing()) {
                                    StatusUtils.hideStatusBar(activity);
                                }
                            }
                        });
*/
            }
        }
    }


    /**
     * 隐藏状态栏
     * @param activity
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void hideStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }



    /**
     * 显示状态栏
     * @param activity
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void showStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
            View decorView = activity.getWindow().getDecorView();
            if(decorView != null && decorView.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_VISIBLE) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }

    /**
     * 暂停 视频 js（如果暂停了视频则回调，在OnResume时候根据回调判断是否需要继续播放视频）
     * @return
     */
    public static String getPauseVideoJS() {
        return "javascript: var video = document.getElementsByTagName('video'); if(!Array.prototype.isPrototypeOf(video) && video.length != 0){ if(!video[0].paused){video[0].pause();onClick.videoPause();}}";
    }

    /**
     * 暂停 audio js
     * @return
     */
    public static String getPauseAudioJS() {
        return "javascript: var audio = document.getElementsByTagName('audio'); if(!Array.prototype.isPrototypeOf(audio) && audio.length != 0){ audio[0].pause();}";
    }




    /**
     * 播放视频 js
     * @return
     */
    public static String getPlayVideoJS() {
        return "javascript: var video = document.getElementsByTagName('video'); if(!Array.prototype.isPrototypeOf(video) && video.length != 0){ video[0].play();}";
    }
}
