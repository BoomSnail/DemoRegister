package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.crooods.common.protocol.ResponseT;
import com.crooods.wd.service.IUserStubService;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.util.AndroidBug5497Workaround;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.edit_login_name)
    EditText mEditLoginName;
    @Bind(R.id.edit_login_password)
    EditText mEditLoginPassword;
    @Bind(R.id.layout_login)
    LinearLayout mLayoutLogin;
    @Bind(R.id.text_forget_pwd)
    TextView mTextForgetPwd;
    @Bind(R.id.btn_login)
    Button mBtnLogin;
    @Bind(R.id.layout_bottom)
    RelativeLayout mLayoutBottom;

    private boolean isFromMtTab;
    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);
        setTitle(R.string.title_login);
        getIntentValue();

        AppPref appPref = AppPref.getInstance();
        String loginName = appPref.getLoginName();
        String password = appPref.getPassword();

        if (!TextUtils.isEmpty(loginName)) {
            mEditLoginName.setText(loginName);
        }

        if (!TextUtils.isEmpty(password)) {
            mEditLoginPassword.setText(password);
        }

        appPref.saveUser(null);
        appPref.saveUserProfile(null);
        appPref.saveAccessToken("");
    }

    private void getIntentValue() {
        Intent intent = getIntent();
        if (intent.hasExtra("fromMy")) {
            isFromMtTab = intent.getBooleanExtra("fromMy",false);
        }
    }

    @OnClick(R.id.text_forget_pwd)
    public void gotoForgetPwd(View v){
        startActivity(new Intent(this,ForgetPasswordActivity.class));
    }

    @OnClick(R.id.btn_login)
    public void login(){
        if (CheckPersonalInfo()) {
            doGetTokenSession();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login,menu);
        return true;
    }

    @Override
    protected boolean _onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_register:

                //去register
                startActivity(new Intent(this,RegisterPhoneActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    private void doGetTokenSession() {

        showProgress(R.string.loading_login);
        IUserStubService service = RetrofitUtil.createApi(IUserStubService.class);
        service.obtainAccessToken("mobile-client", "mobile", "password", "read,write",
                mEditLoginName.getText().toString().trim(), mEditLoginPassword.getText().toString().trim(),
                new RetrofitUtil.ActivityCallback<ResponseT<JSONObject>>(this) {
                    @Override
                    public void success(ResponseT<JSONObject> jsonObjectResponseT, Response response) {
                        super.success(jsonObjectResponseT, response);
                        String accessToken = jsonObjectResponseT.getBizData().getString("value");
                        if (!TextUtils.isEmpty(accessToken)) {
                            AppPref.getInstance().saveAccessToken(accessToken);
                            AppPref.getInstance().setLoginName(mEditLoginName.getText().toString().trim());
                            AppPref.getInstance().setPassword(mEditLoginPassword.getText().toString().trim());
                            hideProgress();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            hideProgress();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        hideProgress();
                    }
                });
    }

    private boolean CheckPersonalInfo() {
        if (TextUtils.isEmpty(mEditLoginName.getText().toString().trim())) {
            Toast.makeText(LoginActivity.this, R.string.account_is_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mEditLoginPassword.getText().toString().trim())) {
            Toast.makeText(LoginActivity.this, R.string.password_is_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressDialog = null;
    }

}
