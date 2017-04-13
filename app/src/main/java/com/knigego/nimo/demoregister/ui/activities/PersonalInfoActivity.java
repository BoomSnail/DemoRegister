package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.ui.fragment.UserInfoFragment;

public class PersonalInfoActivity extends BaseActivity {

    private String userName;
    private long userId;
    private String userRole;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_personal_info);

        getIntentValue();
        if (!TextUtils.isEmpty(userName)) {
            updateUserTitle(userName);
        }
        UserInfoFragment userInfoFragment = UserInfoFragment.newInstance(userId,userRole);
        getSupportFragmentManager().beginTransaction().
                add(R.id.frameLayout,userInfoFragment).commitAllowingStateLoss();
    }

    public void updateUserTitle(String userName) {
        setTitle(userName);
    }

    private void getIntentValue() {

        Intent intent = getIntent();
        userName = intent.getStringExtra("nickName");
        userId = intent.getLongExtra("userId", 0);
        userRole = intent.getStringExtra("userRole");

    }
}
