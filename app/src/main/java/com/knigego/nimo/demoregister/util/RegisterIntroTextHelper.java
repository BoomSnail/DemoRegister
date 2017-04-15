package com.knigego.nimo.demoregister.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.knigego.nimo.demoregister.AppConfig;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.activities.RegisterPhoneActivity;

/**
 * Created by ThinkPad on 2017/4/15.
 */

public class RegisterIntroTextHelper {
    public static void show(Context context, TextView textView) {

        String info1 = context.getString(R.string.register_info_1);
        String info2 = context.getString(R.string.register_info_2);
        String info3 = context.getString(R.string.register_info_3);
        String info4 = context.getString(R.string.register_info_4);

        String termOfService = context.getString(R.string.register_terms_of_service);
        String privacyPolicy = context.getString(R.string.register_privacy_policy);
        String contentAgreement = context.getString(R.string.register_content_agreement);

        SpannableString termOfServiceSpan = new SpannableString(termOfService);
        SpannableString privacyPolicySpan = new SpannableString(privacyPolicy);
        SpannableString contentAgreementSpan = new SpannableString(contentAgreement);

        ClickableSpan termsOfServiceClickableSpan = new CustomCliclableSpan(context,CustomCliclableSpan.TERMS_OF_SERVICE);
        ClickableSpan privacyPolicyClickableSpan = new CustomCliclableSpan(context,CustomCliclableSpan.PRIVACY_POLICY);
        ClickableSpan contentAgreementClickableSpan = new CustomCliclableSpan(context,CustomCliclableSpan.CONTENT_AGREEMENT);

        termOfServiceSpan.setSpan(termsOfServiceClickableSpan,0,termOfService.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyPolicySpan.setSpan(privacyPolicyClickableSpan,0,privacyPolicy.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentAgreementSpan.setSpan(contentAgreementClickableSpan,0,contentAgreement.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(info1);
        textView.append(termOfServiceSpan);
        textView.append(info2);
        textView.append(privacyPolicySpan);
        textView.append(info3);
        textView.append(contentAgreementSpan);
        textView.append(info4);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private static class CustomCliclableSpan extends ClickableSpan{
        public static final String TERMS_OF_SERVICE = "termsOfService";//服务条款
        public static final String PRIVACY_POLICY = "privacyPolicy";//隐私政策
        public static final String CONTENT_AGREEMENT = "contentAgreement";//内容发布协议

        private Context mContext;
        private String mType = TERMS_OF_SERVICE;

        public CustomCliclableSpan(Context context, String type) {
            mContext = context;
            mType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(mContext.getResources().getColor(R.color.colorAccent));
        }

        @Override
        public void onClick(View widget) {
            if (mType.equals(TERMS_OF_SERVICE)) {
                UIHelper.goToWebView(mContext, AppConfig.PATH_AGREEMENT,"");
            } else if (mType.equals(PRIVACY_POLICY)) {
                UIHelper.goToWebView(mContext,AppConfig.PATH_PRIVACY,"");
            } else if (mType.equals(CONTENT_AGREEMENT)) {
                UIHelper.goToWebView(mContext,AppConfig.PATH_PUBLISH,"");

            }
        }
    }
}
