package com.knigego.nimo.demoregister.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.dto.StringWrapper;
import com.crooods.wd.service.IUserStubService;
import com.knigego.nimo.demoregister.AppConstants;
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

public class RegisterSMSCodeActivity extends BaseActivity {

    @Bind(R.id.text_code_tip)
    TextView mTextCodeTip;
    @Bind(R.id.edit_sms_code)
    EditText mEditSmsCode;
    @Bind(R.id.text_resend)
    TextView mTextResend;
    @Bind(R.id.btn_next_step)
    Button mBtnNextStep;

    private String phoneNum;
    private int mType;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register_smscode);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);

        getIntentValue();

        if (mType == AppConstants.TYPE_PHONE_REGISTER) {
            setTitle(R.string.title_register);
            mTextCodeTip.setText(R.string.register_sms_code);
            mEditSmsCode.setHint(R.string.register_sms_code_hint);
        } else if (mType == AppConstants.TYPE_EMAIL_REGISTER) {
            setTitle(R.string.title_register);
            mTextCodeTip.setText(R.string.register_email_code);
            mEditSmsCode.setHint(R.string.register_sms_code_hint);
        } else if (mType == AppConstants.TYPE_PHONE_FORGET_PWD) {
            setTitle(R.string.title_forget_pwd);
            mTextCodeTip.setText(R.string.register_sms_code);
            mEditSmsCode.setHint(R.string.register_sms_code_hint);
        } else if (mType == AppConstants.TYPE_PHONE_ACCOUNT_CHANGE) {//更换手机号码
            setTitle(R.string.title_change_phone);
            mTextCodeTip.setText(R.string.register_sms_code);
            mEditSmsCode.setHint(R.string.register_sms_code_hint);
            mBtnNextStep.setText(R.string.finish);
        } else if (mType == AppConstants.TYPE_EMAIL_ACCOUNT_CHANGE) {
            setTitle(R.string.title_change_email);
            mTextCodeTip.setText(R.string.register_email_code);
            mEditSmsCode.setHint(R.string.register_email_code_hint);
            mBtnNextStep.setText(R.string.finish);
        }
    }


    public void getIntentValue() {
        Intent intent = getIntent();
        phoneNum = intent.getStringExtra(AppConstants.PHONE_MUN);
        mType = intent.getIntExtra(AppConstants.TYPE_REGISTER_OR_FORGET_PWD,AppConstants.TYPE_PHONE_REGISTER);
    }

    @OnClick(R.id.text_resend)
    public void resendSMSCode(View v){
        showProgress(R.string.loading_get_verify_code);
        IUserStubService service = createApi(IUserStubService.class);
        String accType = (mType == AppConstants.TYPE_PHONE_REGISTER
                || mType == AppConstants.TYPE_PHONE_FORGET_PWD
                || mType == AppConstants.TYPE_EMAIL_ACCOUNT_CHANGE) ?"phone":"email";
        String type = (mType == AppConstants.TYPE_PHONE_REGISTER
                || mType == AppConstants.TYPE_EMAIL_REGISTER) ? "register" : "forget";

        if (mType == AppConstants.TYPE_PHONE_ACCOUNT_CHANGE) {
            type = "changePhone";
        }

        if (mType == AppConstants.TYPE_EMAIL_ACCOUNT_CHANGE) {
            type = "changeEmail";
        }
        service.sendSecurityCode(accType,phoneNum,type,
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(this){
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        hideProgress();
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            Toast.makeText(RegisterSMSCodeActivity.this,R.string.register_get_verify_code_success,Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        hideProgress();
                    }
                });

    }

    @OnClick(R.id.btn_next_step)
    public void nextStep(View v){
        if (TextUtils.isEmpty(mEditSmsCode.getText().toString().trim())) {
            Toast.makeText(this,R.string.verify_code_is_empty,Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(R.string.loading);
        IUserStubService iUserStubService = createApi(IUserStubService.class);
        String accType = (mType == AppConstants.TYPE_PHONE_REGISTER
                || mType == AppConstants.TYPE_PHONE_FORGET_PWD
                || mType == AppConstants.TYPE_PHONE_ACCOUNT_CHANGE) ? "phone" : "email";
        String type = (mType == AppConstants.TYPE_PHONE_REGISTER || mType == AppConstants.TYPE_EMAIL_REGISTER) ? "register" : "forget";
        if (mType == AppConstants.TYPE_PHONE_ACCOUNT_CHANGE) {
            type = "changePhone";
        }
        if(mType == AppConstants.TYPE_EMAIL_ACCOUNT_CHANGE){
            type = "changeEmail";
        }

        iUserStubService.checkSecurityCode(accType,phoneNum,type,mEditSmsCode.getText().toString().trim(),
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(this){
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            if (mType == AppConstants.TYPE_PHONE_ACCOUNT_CHANGE ||
                                    mType == AppConstants.TYPE_EMAIL_ACCOUNT_CHANGE) {
                                changeAccount();
                            } else {
                                hideProgress();
                                Intent intent = new Intent(RegisterSMSCodeActivity.this, RegisterSettingPasswordActivity.class);
                                intent.putExtra(AppConstants.PHONE_MUN, phoneNum);
                                intent.putExtra(AppConstants.TYPE_REGISTER_OR_FORGET_PWD, mType);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(RegisterSMSCodeActivity.this,R.string.register_check_verify_code_fail,Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        hideProgress();
                    }
                });

    }

    private void changeAccount() {
        IUserStubService stubService = createApi(IUserStubService.class);
        if (mType == AppConstants.TYPE_PHONE_ACCOUNT_CHANGE) {
            stubService.changePhone(AppPref.getInstance().getAccessToken(),phoneNum,
                    new OnChangeAccountCallback(this));
        } else if (mType == AppConstants.TYPE_EMAIL_ACCOUNT_CHANGE) {
            stubService.changeEmail(AppPref.getInstance().getAccessToken(),phoneNum,
                    new OnChangeAccountCallback(this));

        }
    }


    private class OnChangeAccountCallback extends RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>{

        public OnChangeAccountCallback(Context context) {
            super(context);
        }

        @Override
        public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
            super.success(stringWrapperResponseT, response);
            hideProgress();
            if(stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)){
                AppPref.getInstance().setLoginName(phoneNum);
                finish();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            super.failure(error);
            hideProgress();
        }
    }


}
