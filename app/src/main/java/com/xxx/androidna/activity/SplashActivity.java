package com.xxx.androidna.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;

import com.xxx.androidna.MainActivity;
import com.xxx.androidna.R;
import com.xxx.base.app.BaseActivity;

import butterknife.BindView;

/**
 * Created by niuapp on 2018/10/9 10:31.
 * Project : AndroidNA.
 * Email : 345485985@qq.com
 * -->
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.tvTimeNum)
    TextView tvTimeNum;
    @BindView(R.id.ivAdImage)
    ImageView ivAdImage;

    @Override
    protected int getContentView() {
        return R.layout.activity_splash;
    }

    /**
     * 手动启动
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        if (context instanceof Activity) {
            context.startActivity(intent);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {


        tvTimeNum.setOnClickListener(v -> skip());
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (isFinishing()) return;
                tvTimeNum.setText("跳过广告 " + String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                if (isFinishing()) return;
                tvTimeNum.setText("跳过广告 " + 0);
                skip();
            }
        }.start();
    }

    private void skip() {
        MainActivity.start(this);
    }
}
