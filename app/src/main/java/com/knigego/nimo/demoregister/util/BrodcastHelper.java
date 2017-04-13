package com.knigego.nimo.demoregister.util;

import android.content.Context;
import android.content.Intent;

import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.ui.activities.ReleaseMomentActivity;

/**
 * 广播帮助
 * Created by ThinkPad on 2017/3/29.
 */

public class BrodcastHelper {
    //更新个人主页的关注数目
    public static void sendUserInfoUpdateAction(Context context){
        Intent intent = new Intent(AppConstants.ACTION_USER_INFO_UPDATE);
        context.sendBroadcast(intent);
    }

    public static void sendDeleteStageAction(Context context, long id) {
    }

    public static void sendDeleteMomentAction(Context context, long id) {
    }

    public static void sendMomentUpdateAction(Context context) {

    }
}
