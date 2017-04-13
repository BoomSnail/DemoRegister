package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.knigego.nimo.demoregister.AppConfig;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.util.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class ReleaseChooseActivity extends BaseActivity {

    @Bind(R.id.text_video)
    TextView mTextVideo;
    @Bind(R.id.image_video_select)
    ImageView mImageVideoSelect;
    @Bind(R.id.layout_video)
    LinearLayout mLayoutVideo;
    @Bind(R.id.text_image)
    TextView mTextImage;
    @Bind(R.id.image_image_select)
    ImageView mImageImageSelect;
    @Bind(R.id.layout_image)
    LinearLayout mLayoutImage;
    @Bind(R.id.text_desc)
    TextView mTextDesc;
    @Bind(R.id.edit_content)
    EditText mEditContent;
    @Bind(R.id.btn_next_step)
    Button mBtnNextStep;
    @Bind(R.id.checkbox_agreement)
    CheckBox mCheckboxAgreement;
    @Bind(R.id.text_agreement)
    TextView mTextAgreement;

    private boolean isVideoChoose = true;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_release_stage);
        ButterKnife.bind(this);
        setTitle(R.string.title_release);
        updateUI();
    }

    private void updateUI() {
        if (isVideoChoose) {
            mLayoutVideo.setSelected(true);
            mLayoutImage.setSelected(false);
            mImageVideoSelect.setVisibility(View.VISIBLE);
            mImageImageSelect.setVisibility(View.GONE);
            mTextDesc.setText(R.string.video_desc);
        } else {
            mLayoutVideo.setSelected(false);
            mLayoutImage.setSelected(true);
            mImageImageSelect.setVisibility(View.VISIBLE);
            mImageVideoSelect.setVisibility(View.GONE);
            mTextDesc.setText(R.string.image_desc);
        }
    }


    @OnClick({R.id.layout_video, R.id.layout_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_video:
                isVideoChoose = true;
                break;
            case R.id.layout_image:
                isVideoChoose = false;
                break;
        }
        updateUI();
    }

    @OnClick(R.id.btn_next_step)
    public void nextStep() {
        if (mCheckboxAgreement.isChecked()) {
            if (isVideoChoose) {
                Intent intent = new Intent(this, ReleaseVideoActivity.class);
                intent.putExtra(AppConstants.RELEASE_CONTENT_KEY, mEditContent.getText().toString().trim());
                startActivity(intent);
                finish();
            } else {
                if (TextUtils.isEmpty(mEditContent.getText().toString().trim())) {
                    Toast.makeText(this, R.string.release_content_hint, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, ReleaseImageActivity.class);
                intent.putExtra(AppConstants.RELEASE_CONTENT_KEY, mEditContent.getText().toString().trim());
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(this, R.string.agreement_unselect, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.text_agreement)
    public void agreementClick() {
        UIHelper.goToWebView(this, AppConfig.PATH_PUBLISH, "");
    }

//    @OnClick(R.id.checkbox_agreement)
//    public void agreementCheckBoxChange(boolean isCheck) {
//        if (isCheck) {
//            mBtnNextStep.setBackgroundResource(R.color.colorAccent);
//            mTextAgreement.setTextColor(getResources().getColor(R.color.colorAccent));
//        } else {
//            mBtnNextStep.setBackgroundResource(R.color.disable_color);
//            mTextAgreement.setTextColor(getResources().getColor(R.color.disable_color));
//        }
//    }


    @OnCheckedChanged(R.id.checkbox_agreement)
    public void onViewClicked(boolean isCheck) {
        if (isCheck) {
            mBtnNextStep.setBackgroundResource(R.color.colorAccent);
            mBtnNextStep.setEnabled(true);
            mTextAgreement.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            mBtnNextStep.setEnabled(false);
            mBtnNextStep.setBackgroundResource(R.color.disable_color);
            mTextAgreement.setTextColor(getResources().getColor(R.color.disable_color));
        }
    }
}
