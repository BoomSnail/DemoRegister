package com.knigego.nimo.demoregister.util;

import android.content.Context;
import android.content.Intent;

import com.crooods.wd.dto.UserDto;
import com.crooods.wd.dto.post.StageDto;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.ui.activities.MyFansActivity;
import com.knigego.nimo.demoregister.ui.activities.MyFavoriteActivity;
import com.knigego.nimo.demoregister.ui.activities.PersonalInfoActivity;
import com.knigego.nimo.demoregister.ui.activities.StageDetailActivity;
import com.knigego.nimo.demoregister.ui.activities.UserInfoEditActivity;
import com.knigego.nimo.demoregister.ui.activities.VideoPlayActivity;

/**
 * activity 跳转

 * Created by ThinkPad on 2017/3/29.
 */

public class UIHelper {
    //跳转至personalActivity
    public static void goToPersonalInfo(Context context,String nickName,long userId,String userRole){
        Intent intent = new Intent(context,PersonalInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("nickName",nickName);
        intent.putExtra("userId",userId);
        intent.putExtra("userRole",userRole);
        context.startActivity(intent);
    }

    public static void goToVideoPlayActivity(Context context, String videoUrl) {
        Intent intent = new Intent(context,VideoPlayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("videoUrl",videoUrl);
        context.startActivity(intent);
    }

    public static void goToDetail(Context context, int type, StageDto stageDto, boolean isComment) {
        Intent intent = new Intent(context,StageDetailActivity.class);
        intent.putExtra(AppConstants.STAGE_ITEM,stageDto);
        intent.putExtra(AppConstants.IS_COMMENT,isComment);
        intent.putExtra(AppConstants.TYPE,type);
        context.startActivity(intent);

    }

    public static void gotoMyFans(Context context, Long userId) {
        Intent intent = new Intent(context, MyFansActivity.class);
        intent.putExtra("userId",userId);
        context.startActivity(intent);
    }

    public static void goToMyFavorite(Context context) {

        context.startActivity(new Intent(context,MyFavoriteActivity.class));
    }

    public static void goToEditUserInfo(Context context, boolean canEdit, UserDto userDto) {
        Intent intent = new Intent(context,UserInfoEditActivity.class);
        intent.putExtra("canEdit",canEdit);
        intent.putExtra("user",userDto);
        context.startActivity(intent);
    }

    public static void goToWebView(Context context, String linkUrl, String name) {

    }
}
