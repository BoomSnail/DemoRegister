package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;


public class WelcomeActivity extends BaseActivity {

    @Override
    protected void _onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_welcome);

//        View view = getWindow().getDecorView();
//        int flag = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        view.setSystemUiVisibility(flag);
        hideToolbar(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                finish();
            }
        },2000);
    }
}
