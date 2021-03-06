package com.sibozn.peo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sibozn.peo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/1/13.
 */

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.tv_underline)
    TextView tv_underline;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        handler = new Handler();
        gotoYindaoye();
    }

    @OnClick({R.id.tv_underline})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_underline:
                handler.removeCallbacksAndMessages(null);
                tv_underline.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
        }
    }

    private void gotoYindaoye() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 3000);
    }
}
