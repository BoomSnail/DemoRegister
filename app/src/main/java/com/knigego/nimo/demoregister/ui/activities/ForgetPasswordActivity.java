package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.dto.ListWrapper;
import com.crooods.common.protocol.dto.StringWrapper;
import com.crooods.wd.domain.AppUser;
import com.crooods.wd.service.IUserStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.util.AndroidBug5497Workaround;
import com.knigego.nimo.demoregister.util.CommonUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ForgetPasswordActivity extends BaseActivity {

    @Bind(R.id.edit_forget_pwd)
    EditText mEditForgetPwd;
    @Bind(R.id.btn_next_step)
    Button mBtnNextStep;
    @Bind(R.id.layout_bottom)
    RelativeLayout mLayoutBottom;

    private int mType;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);
        setTitle(R.string.title_forget_pwd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forget_pwd, menu);
        return true;

    }

    @Override
    protected boolean _onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_go_login:
                finish();
                break;
            default:
                break;
        }
        return true;

    }

    @OnClick(R.id.btn_next_step)
    public void onViewClicked() {
        String account = mEditForgetPwd.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, R.string.account_is_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (CommonUtils.isEmail(account)) {
            mType = AppConstants.TYPE_EMAIL_FORGET_PWD;
            checkEmailExist(account);
        } else {
            mType = AppConstants.TYPE_PHONE_FORGET_PWD;
            checkPhoneExist(account);
        }
    }

    private void checkPhoneExist(String account) {
        showProgress(R.string.loading);
        IUserStubService iUserStubService = createApi(IUserStubService.class);
        iUserStubService.isUserExistsByPhone(account, new RetrofitUtil.ActivityCallback<ResponseT<ListWrapper<AppUser>>>(this) {
            @Override
            public void success(ResponseT<ListWrapper<AppUser>> listWrapperResponseT, Response response) {
                super.success(listWrapperResponseT, response);
                if (listWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                    getVerifyCode();
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

    private void checkEmailExist(String account) {
        showProgress(R.string.loading_get_verify_code);
        IUserStubService stubService = createApi(IUserStubService.class);
        stubService.isUserExistsByEmail(account,
                new RetrofitUtil.ActivityCallback<ResponseT<ListWrapper<AppUser>>>(this) {
                    @Override
                    public void success(ResponseT<ListWrapper<AppUser>> listWrapperResponseT, Response response) {
                        super.success(listWrapperResponseT, response);
                        if (listWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            getVerifyCode();
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

    private void getVerifyCode() {
        IUserStubService stubService = createApi(IUserStubService.class);
        String accType = mType == AppConstants.TYPE_EMAIL_FORGET_PWD ? "email" : "phone";
        stubService.sendSecurityCode(accType, mEditForgetPwd.getText().toString().trim(), "forget",
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(this) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        hideProgress();
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            //跳转至验证码界面

                            Intent intent = new Intent(ForgetPasswordActivity.this, RegisterSMSCodeActivity.class);
                            intent.putExtra(AppConstants.PHONE_MUN, mEditForgetPwd.getText().toString().trim());
                            intent.putExtra(AppConstants.TYPE_REGISTER_OR_FORGET_PWD, mType);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        hideProgress();
                    }
                });
    }
}
