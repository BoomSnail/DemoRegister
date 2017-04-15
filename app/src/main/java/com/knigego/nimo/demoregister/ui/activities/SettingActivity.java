package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crooods.wd.enums.RoleEnum;
import com.knigego.nimo.demoregister.AppConfig;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.util.ActivityManagers;
import com.knigego.nimo.demoregister.util.AppUpdateUtils;
import com.knigego.nimo.demoregister.util.CommonUtils;
import com.knigego.nimo.demoregister.util.DataCleanManager;
import com.knigego.nimo.demoregister.util.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.layout_account)
    LinearLayout mLayoutAccount;
    @Bind(R.id.checkbox_use_mobile_network)
    CheckBox mCheckboxUseMobileNetwork;
    @Bind(R.id.layout_use_mobile)
    LinearLayout mLayoutUseMobile;
    @Bind(R.id.text_cache_size)
    TextView mTextCacheSize;
    @Bind(R.id.layout_clear_cache)
    LinearLayout mLayoutClearCache;
    @Bind(R.id.layout_recommend)
    LinearLayout mLayoutRecommend;
    @Bind(R.id.layout_blacklist)
    LinearLayout mLayoutBlacklist;
    @Bind(R.id.text_version)
    TextView mTextVersion;
    @Bind(R.id.layout_version)
    LinearLayout mLayoutVersion;
    @Bind(R.id.layout_help_and_feedback)
    LinearLayout mLayoutHelpAndFeedback;
    @Bind(R.id.layout_terms)
    LinearLayout mLayoutTerms;
    @Bind(R.id.text_logout)
    TextView mTextLogout;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setTitle(R.string.title_setting);
        init();
    }

    private void init() {
        String version = CommonUtils.getAppVersionName(this);
        mTextVersion.setText("v" + version);
        if (AppPref.getInstance().isMobileNetUsable()) {
            mCheckboxUseMobileNetwork.setChecked(true);
        } else {
            mCheckboxUseMobileNetwork.setChecked(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasLogin()) {
            mTextLogout.setText(R.string.logout);
        } else {
            mTextLogout.setText(R.string.login);
        }
    }

    private boolean hasLogin() {
        if (!TextUtils.isEmpty(AppPref.getInstance().getAccessToken())) {
            return true;
        }
        return false;
    }
    @OnClick(R.id.layout_version)
    public void checkUpdate(){
//        AppUpdateUtils.checkPgyUpdate(this,false);
    }

    @OnClick(R.id.text_logout)
    public void logout(){
        if (hasLogin()) {//如果已登录，点击按钮则退出登录
            AppPref appPref = AppPref.getInstance();
            appPref.saveAccessToken("");
            appPref.saveUser(null);
            appPref.saveUserProfile(null);
            appPref.saveStages(null);
            appPref.saveUserId(0);
            appPref.saveUserRole(RoleEnum.c.getCode());
            appPref.saveMyStages(null);
            appPref.setLoginName("");
            appPref.setPassword("");


            ActivityManagers.getManager().finishAllActivity();
            startActivity(new Intent(this, MainActivity.class));
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }

    @OnClick({R.id.layout_account, R.id.checkbox_use_mobile_network, R.id.layout_clear_cache, R.id.layout_recommend, R.id.layout_blacklist, R.id.layout_help_and_feedback, R.id.layout_terms})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_account:
                break;
            case R.id.checkbox_use_mobile_network:
                if (mCheckboxUseMobileNetwork.isChecked()) {
                    AppPref.getInstance().setMoibleNetUsable(true);
                } else {
                    AppPref.getInstance().setMoibleNetUsable(false);
                }
                break;
            case R.id.layout_clear_cache:
                clearAllCache();
                break;
            case R.id.layout_recommend:
                break;
            case R.id.layout_blacklist:
                break;
            case R.id.layout_help_and_feedback:
                break;
            case R.id.layout_terms:
                UIHelper.goToWebView(this, AppConfig.PATH_AGREEMENT,"");
                break;
        }
    }

    private void clearAllCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataCleanManager.clearAllCache(SettingActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SettingActivity.this,R.string.clear_cache_success,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
