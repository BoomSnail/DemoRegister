package com.knigego.nimo.demoregister.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crooods.common.protocol.ResponseT;
import com.crooods.wd.dto.UserBaseDto;
import com.crooods.wd.dto.UserCountDto;
import com.crooods.wd.dto.UserDto;
import com.crooods.wd.dto.UserProfileDto;
import com.crooods.wd.dto.UserSetInfoDto;
import com.crooods.wd.service.IUserProfileStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.util.GlideCircleTransform;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserInfoEditActivity extends BaseActivity {

    @Bind(R.id.image_avatar)
    ImageView mImageAvatar;
    @Bind(R.id.image_avatar_arrow)
    ImageView mImageAvatarArrow;
    @Bind(R.id.layout_avatar)
    RelativeLayout mLayoutAvatar;
    @Bind(R.id.edit_nickname)
    EditText mEditNickname;
    @Bind(R.id.edit_brief)
    EditText mEditBrief;
    @Bind(R.id.text_address)
    TextView mTextAddress;
    @Bind(R.id.image_address_arrow)
    ImageView mImageAddressArrow;
    @Bind(R.id.layout_address)
    LinearLayout mLayoutAddress;
    @Bind(R.id.text_sex)
    TextView mTextSex;
    @Bind(R.id.image_sex_arrow)
    ImageView mImageSexArrow;
    @Bind(R.id.layout_sex)
    LinearLayout mLayoutSex;

    private boolean isFromRegister;
    private UserDto mUserDto;
    private boolean mCanEdit;
    private String[] mSexArray;

    private String mUploadAvataUrl;
    private int mChooseNum;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_info_edit);
        ButterKnife.bind(this);
        setTitle(R.string.title_edit_address);

        getIntentValue();
        if (mCanEdit) {
            setTitle(R.string.title_userinfo_edit);
            mImageAvatarArrow.setVisibility(View.VISIBLE);
            mImageAddressArrow.setVisibility(View.VISIBLE);
            mImageSexArrow.setVisibility(View.VISIBLE);
            mEditNickname.setEnabled(true);
            mEditBrief.setEnabled(true);
            mEditBrief.setHeight(R.string.brief_hint);
        } else {
            setTitle(R.string.title_userinfo_check);
            mImageAvatarArrow.setVisibility(View.GONE);
            mImageAddressArrow.setVisibility(View.GONE);
            mImageSexArrow.setVisibility(View.GONE);
            mEditNickname.setEnabled(false);
            mEditBrief.setEnabled(false);
            mEditBrief.setHint(R.string.brief_hint_2);
        }
        mSexArray = new String[]{
                getString(R.string.my_sex_girl),
                getString(R.string.my_sex_boy)
        };
        initData();
    }

    private void initData() {
        UserBaseDto userBaseDto ;
        if (mCanEdit) {//可以编辑，则表明是自己的个人信息，直接从存储中取出
            UserProfileDto userProfileDto = AppPref.getInstance().getUserProfile();
            if (userProfileDto == null) {
                return;
            }
            userBaseDto = userProfileDto.getUserBaseDto();
        } else {//查看用户的信息，从传过来的userDto取出
            if (mUserDto == null) {
                return;
            }
            userBaseDto = mUserDto.getUserBaseDto();
        }

        if (userBaseDto == null) {
            return;
        }
        mUploadAvataUrl = userBaseDto.getAvator();
        //头像
        Glide.with(this).load(mUploadAvataUrl).transform(new GlideCircleTransform(this))
                .placeholder(R.drawable.rc_ic_def_msg_portrait)
                .into(mImageAvatar);
        mEditNickname.setText(TextUtils.isEmpty(userBaseDto.getNickName())?"":userBaseDto.getNickName());
        mEditBrief.setText(TextUtils.isEmpty(userBaseDto.getIntro())?"":userBaseDto.getIntro());
        mTextAddress.setText(TextUtils.isEmpty(userBaseDto.getAddress())?"":userBaseDto.getAddress());

        String sexText = " ";
        String sex = userBaseDto.getSex();
        if (!TextUtils.isEmpty(sex)) {
            if (sex.equals("Boy")) {
                sexText = getString(R.string.my_sex_boy);
                mChooseNum = 0;
            } else if (sex.equals("Girl")) {
                sexText = getString(R.string.my_sex_girl);
                mChooseNum = 1;
            }
        }
        mTextSex.setText(sexText);
    }

    private void getIntentValue() {
        Intent intent = getIntent();
        mUserDto = (UserDto) intent.getSerializableExtra("user");
        mCanEdit = intent.getBooleanExtra("canEdit", true);
        if (isFromRegister) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean backEvent() {
        if (isFromRegister) {
            return false;
        }
        return super.backEvent();
    }
    @OnClick(R.id.layout_avatar)
    public void editAvatar(){
        if (mCanEdit) {

        }
    }
    @OnClick(R.id.layout_address)
    public void editAddress(){
        if (mCanEdit) {
            startActivityForResult(new Intent(this,UserAddressEditActivity.class),1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mCanEdit) {
            getMenuInflater().inflate(R.menu.menu_userinfo_edit,menu);
        }
        return true;
    }

    @OnClick(R.id.layout_sex)
    public void editSex(){
        if (mCanEdit) {
            chooseSexDialog();
        }
    }

    private void chooseSexDialog() {
        int defaultCheckedItem = -1;
        String sex = mTextSex.getText().toString().trim();
        for (int i = 0; i < mSexArray.length; i++) {
            if (sex.equals(mSexArray[i])) {
                defaultCheckedItem = i;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_choose_sex);
        builder.setSingleChoiceItems(mSexArray, defaultCheckedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mChooseNum = which;
            }
        });
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mChooseNum != -1) {
                    mTextSex.setText(mSexArray[mChooseNum]);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected boolean _onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                saveUserInfo();
                break;
            default:
                break;
        }
        return true;
    }



    private void saveUserInfo() {
        if (TextUtils.isEmpty(mEditNickname.getText().toString().trim())) {
            Toast.makeText(this,R.string.nickname_is_empty,Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mTextAddress.getText().toString().trim())) {
            Toast.makeText(this,R.string.address_is_empty,Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mTextSex.getText().toString())) {
            Toast.makeText(this,R.string.sex_is_empty,Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(R.string.loading_save);
        IUserProfileStubService service = createApi(IUserProfileStubService.class);
        UserSetInfoDto userSetInfoDto = new UserSetInfoDto();
        userSetInfoDto.setAvatar(mUploadAvataUrl);
        userSetInfoDto.setNickName(mEditNickname.getText().toString().trim());
        userSetInfoDto.setAddress(mTextAddress.getText().toString().trim());
        userSetInfoDto.setIntro(mEditBrief.getText().toString().trim());
        userSetInfoDto.setUserId(AppPref.getInstance().getUserId());
        UserProfileDto userProfileDto = AppPref.getInstance().getUserProfile();

        String sex = "";
        if (userProfileDto != null && userProfileDto.getUserBaseDto().getSex() != null) {
            sex = AppPref.getInstance().getUserProfile().getUserBaseDto().getSex();
        }

        if (mChooseNum != -1) {
            if (mChooseNum == 0) {
                sex = "Girl";
            } else if (mChooseNum == 1) {
                sex = "Boy";
            }
        }
        userSetInfoDto.setSex(sex);

        service.updateUserBaseInfo(AppPref.getInstance().getAccessToken(),userSetInfoDto,
                new RetrofitUtil.ActivityCallback<ResponseT<UserBaseDto>>(this){
                    @Override
                    public void success(ResponseT<UserBaseDto> userBaseDtoResponseT, Response response) {
                        super.success(userBaseDtoResponseT, response);
                        hideProgress();
                        if (userBaseDtoResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            UserBaseDto userBaseDto = userBaseDtoResponseT.getBizData();
                            UserProfileDto userProfileDto1 = AppPref.getInstance().getUserProfile();
                            if (userProfileDto1 != null) {
                                userProfileDto1.setUserBaseDto(userBaseDto);
                            } else {
                                userProfileDto1 = new UserProfileDto();
                                userProfileDto1.setUserBaseDto(userBaseDto);
                                UserCountDto userCountDto = new UserCountDto();
                                userProfileDto1.setUserCountDto(userCountDto);
                            }
                            AppPref.getInstance().saveUserProfile(userProfileDto1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                String address = data.getStringExtra("address");
                mTextAddress.setText(address);
            }
        }
    }
}
