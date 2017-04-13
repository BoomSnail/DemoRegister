package com.knigego.nimo.demoregister.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.crooods.common.protocol.ResponseT;
import com.crooods.wd.service.IUserStubService;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends BaseActivity {

    private EditText mLoginName;
    private EditText mLoginPassword;
    private Button mLogin;

    private String userName;
    private ProgressDialog mProgressDialog;
    @Override
    protected void _onCreate(Bundle savedInstanceState) {
//        Window window = getWindow();
//        window.addFlags(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initView();
        AppPref appPref = AppPref.getInstance();
        String password = appPref.getPassword();

        if (!TextUtils.isEmpty(userName)) {
            mLoginName.setText(userName);
        }

        if (!TextUtils.isEmpty(password)) {
            mLoginPassword.setText(password);
        }

//        if (!TextUtils.isEmpty(appPref.getAccessToken())) {
//            startActivity(new Intent(this,MainActivity.class));
//        }
    }

    private void initView() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在校验登录");
        mProgressDialog.setTitle("温馨提示");
        mProgressDialog.setCancelable(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mLoginName = (EditText) findViewById(R.id.login_name);
        mLoginPassword = (EditText) findViewById(R.id.login_password);
        mLogin = (Button) findViewById(R.id.login);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean yes = CheckPersonalInfo();
                if (yes) {
                    doGetTokenSession();
                }
            }
        });
    }

    private void doGetTokenSession() {

        mProgressDialog.show();
        IUserStubService service = RetrofitUtil.createApi(IUserStubService.class);
        service.obtainAccessToken("mobile-client","mobile", "password", "read,write",
                mLoginName.getText().toString().trim(),mLoginPassword.getText().toString().trim(),
                new RetrofitUtil.ActivityCallback<ResponseT<JSONObject>>(this){
                    @Override
                    public void success(ResponseT<JSONObject> jsonObjectResponseT, Response response) {
                        super.success(jsonObjectResponseT, response);
                        String accessToken = jsonObjectResponseT.getBizData().getString("value");
                        if (!TextUtils.isEmpty(accessToken)) {
                            AppPref.getInstance().saveAccessToken(accessToken);

                            Log.i("---------------\n", "success: " + accessToken);
                            AppPref.getInstance().setLoginName(mLoginName.getText().toString().trim());
                            AppPref.getInstance().setPassword(mLoginPassword.getText().toString().trim());
                                Toast.makeText(LoginActivity.this,"dengluchenggong ",Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        }else {
                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        mProgressDialog.dismiss();
                    }
                });
    }

    private boolean CheckPersonalInfo() {
        if (TextUtils.isEmpty(mLoginName.getText().toString().trim()))  {
            Toast.makeText(LoginActivity.this,"用户名为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mLoginPassword.getText().toString().trim())) {
            Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
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
