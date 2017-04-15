package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.dto.ListWrapper;
import com.crooods.common.protocol.dto.StringWrapper;
import com.crooods.wd.domain.AppUser;
import com.crooods.wd.enums.AccountTypeEnum;
import com.crooods.wd.service.IUserStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.util.AndroidBug5497Workaround;
import com.knigego.nimo.demoregister.util.RegisterIntroTextHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegisterPhoneActivity extends BaseActivity {

    @Bind(R.id.edit_phone_num)
    EditText mEditPhoneNum;
    @Bind(R.id.text_register_intro)
    TextView mTextRegisterIntro;
    @Bind(R.id.text_register_email)
    TextView mTextRegisterEmail;
    @Bind(R.id.btn_next_step)
    Button mBtnNextStep;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register_phone);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);
        setTitle(R.string.title_register);
        RegisterIntroTextHelper.show(this,mTextRegisterIntro);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forget_pwd,menu);
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

    @OnClick(R.id.text_register_email)
    public void goToEmailRegister(View view) {
        startActivity(new Intent(this,RegisterEmailActivity.class));
        finish();
    }

    @OnClick(R.id.btn_next_step)
    public void nextStep(View view) {
        if (isPhoneVaild()) {
            checkPhoneNum();
        }
    }

    private void checkPhoneNum() {

        showProgress(R.string.loading_get_verify_code);
        IUserStubService stubService = createApi(IUserStubService.class);
        stubService.isUserExistsByPhone(mEditPhoneNum.getText().toString().trim(),
                new RetrofitUtil.ActivityCallback<ResponseT<ListWrapper<AppUser>>>(this){
                    @Override
                    public void success(ResponseT<ListWrapper<AppUser>> listWrapperResponseT, Response response) {
                        if (listWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            Toast.makeText(RegisterPhoneActivity.this,R.string.user_is_exist,Toast.LENGTH_SHORT).show();
                            hideProgress();
                        } else if (listWrapperResponseT.getRtnCode().equals("00230005")) {
                            getVerfyCode();
                        } else {
                            super.success(listWrapperResponseT, response);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        hideProgress();
                    }
                });
    }

    private void getVerfyCode() {
        final IUserStubService service = createApi(IUserStubService.class);
        service.sendSecurityCode(AccountTypeEnum.Phone.getCode(),mEditPhoneNum.getText().toString().trim(),
                "register",new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(this){
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        hideProgress();
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            Intent intent = new Intent(RegisterPhoneActivity.this,RegisterSMSCodeActivity.class);
                            intent.putExtra(AppConstants.PHONE_MUN,mEditPhoneNum.getText().toString().trim());
                            intent.putExtra(AppConstants.TYPE_REGISTER_OR_FORGET_PWD,AppConstants.TYPE_PHONE_REGISTER);
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

    private boolean isPhoneVaild() {
        if (TextUtils.isEmpty(mEditPhoneNum.getText().toString().trim())) {
            Toast.makeText(this,R.string.phone_num_is_empty,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
