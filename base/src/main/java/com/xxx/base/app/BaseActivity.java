package com.xxx.base.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.lzy.okgo.OkGo;
import com.xxx.base.BaseApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;


/**
 * Activity 基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected SharedPreferences sp;

    // 静态的 可以得到前台Activity
    private static BaseActivity mForegroundActivity;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        beforOnCreate();
        super.onCreate(savedInstanceState);

        //侧滑
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)//获取当前页面
                .setSwipeBackEnable(true)//设置是否可滑动
                .setSwipeEdge(200)//可滑动的范围。px。200表示为左边200px的屏幕
                .setSwipeEdgePercent(0.2f)//可滑动的范围。百分比。0.2表示为左边20%的屏幕
                .setSwipeSensitivity(0.5f)//对横向滑动手势的敏感程度。0为迟钝 1为敏感
                //    .setScrimColor(Color.BLUE)//底层阴影颜色
                .setClosePercent(0.8f)//触发关闭Activity百分比
                .setSwipeRelateEnable(true)//是否与下一级activity联动(微信效果)。默认关
                .setSwipeRelateOffset(500)//activity联动时的偏移量。默认500px。
                .setDisallowInterceptTouchEvent(false);//不抢占事件，默认关（事件将先由子View处理再由滑动关闭处理）

        //添加到已经打开的Activity集合
        BaseApplication.addActivity(this);


        //设置内容页布局
        int contentViewRes = getContentView();
        if (contentViewRes != 0) {
            mContentView = View.inflate(this, contentViewRes, null);
        }
        if (mContentView != null) {
            setContentView(mContentView);
            ButterKnife.bind(this, mContentView);
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        //初始化View
        initView(savedInstanceState);
    }

    protected void beforOnCreate() {
    }


    /**
     * @return 返回Activity的内容View
     */
    protected abstract @LayoutRes
    int getContentView();

    protected abstract void initView(Bundle savedInstanceState);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BaseEvent event) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseActivity.mForegroundActivity = this;

    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseActivity.mForegroundActivity = null;
    }


    /**
     * @return 获取前台Activity
     */
    public static BaseActivity getForegroundActivity() {
        return mForegroundActivity;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        OkGo.getInstance().cancelTag(this);
        SwipeBackHelper.onDestroy(this);
        EventBus.getDefault().unregister(this);

        //从Activity集合中移除
        BaseApplication.removeActivity(this);
    }


    /**
     * 当前Activity的根布局
     *
     * @return
     */
    public View getCurrentActivityView() {

        if (mContentView == null) {
            View cView = null;
            try {
                cView = getWindow().getDecorView().findViewById(android.R.id.content);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return cView;
        }

        return mContentView;


    }

    protected <T extends View> T $(int resId) {
        return (T) findViewById(resId);
    }
}
