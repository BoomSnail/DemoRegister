package com.knigego.nimo.demoregister.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.crooods.wd.dto.UserProfileDto;
import com.crooods.wd.dto.post.MomentDto;
import com.crooods.wd.dto.post.MomentItemDto;
import com.crooods.wd.dto.post.StageCreateDto;
import com.crooods.wd.dto.post.StageDto;
import com.crooods.wd.enums.RoleEnum;
import com.crooods.wd.service.IFavoritesStubService;
import com.knigego.nimo.demoregister.DemoRegisterApplication;
import com.knigego.nimo.demoregister.ui.model.MediaCacheInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad on 2017/2/24.
 */

public class AppPref extends PreferConfig {

    private static AppPref instance = null;
    private SharedPreferences app = null;
    private static final String PREFERENCE_NAME = "DemoRegister_pref";

    public AppPref(Context context) {
        app = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static AppPref getInstance() {
        if (instance == null) {
            synchronized (AppPref.class) {
                if (instance == null) {
                    instance = new AppPref(DemoRegisterApplication.getContext());
                }
            }
        }
        return instance;
    }

    public void setLoginName(String userName) {
        put(LOGIN_NAME, userName);
    }

    public String getLoginName() {
        return app.getString(LOGIN_NAME, "");
    }

    public void setPassword(String password) {
        put(LOGIN_PASSWORD, password);
    }

    public String getPassword() {
        return app.getString(LOGIN_PASSWORD, "");
    }

    public void saveAccessToken(String accessToken) {
        put(LOGIN_ACCESS_TOKEN, accessToken);
    }

    public String getAccessToken() {
        return app.getString(LOGIN_ACCESS_TOKEN, "");
    }



    public void saveUserProfile(UserProfileDto userProfileDto) {
        if (userProfileDto != null) {
            saveUserId(userProfileDto.getUserBaseDto().getUserId());
            saveUserRole(userProfileDto.getUserBaseDto().getRole());
            put(USER_PROFILE, JSON.toJSONString(userProfileDto));
        } else {
            saveUserId(0);
            saveUserRole(RoleEnum.c.getCode());
            put(USER_PROFILE, "");
        }
    }

    private void saveUserRole(String role) {
        put(USER_ROLE,role);
    }

    public UserProfileDto getUserProfile() {
        String userJsonString = app.getString(USER_PROFILE, "");
        if (!TextUtils.isEmpty(userJsonString)) {
            return JSON.parseObject(userJsonString, UserProfileDto.class);
        }
        return null;
    }

    private void saveUserId(long userId) {
        put(USER_ID, userId);
    }

    public Long getUserId() {
        return app.getLong(USER_ID, 0);
    }

    public void saveStages(List<StageDto> stageDtos) {
        if (stageDtos != null) {
            put(STAGE_LIST_DATA, JSON.toJSONString(stageDtos));
        } else {
            put(STAGE_LIST_DATA, "");
        }
    }

    public List<StageDto> getStages() {
        String stagesJsonString = app.getString(STAGE_LIST_DATA, "");
        if (!TextUtils.isEmpty(stagesJsonString)) {
            return JSON.parseArray(stagesJsonString, StageDto.class);
        }
        return new ArrayList<>();
    }

    public void saveMyStages(List<StageDto> stageDtos){
        long userId = getUserId();
        if (stageDtos != null) {
            put(MY_STAGE_LIST_DATA + "_" + userId, JSON.toJSONString(stageDtos));
        } else {
            put(MY_STAGE_LIST_DATA + "_" + userId, "");
        }
    }

    public List<StageDto> getMyStages(){
        long useId = getUserId();
        String jsonString = app.getString(MY_STAGE_LIST_DATA,"");
        if (!TextUtils.isEmpty(jsonString)) {
            return JSON.parseArray(jsonString, StageDto.class);
        }
            return new ArrayList<>();

    }
    public String getUserRole() {
        return app.getString(USER_ROLE, RoleEnum.c.getCode());
    }

    public List<MediaCacheInfo> getMyMediaCache() {
        long usrID = getUserId();
        String jsonString = app.getString(MY_MEDIA_CACHE_DATA + "_" + usrID, "");
        if (!TextUtils.isEmpty(jsonString)) {
            return JSON.parseArray(jsonString, MediaCacheInfo.class);
        }
        return new ArrayList<>();
    }

    public void saveMyMediaCache(List<MediaCacheInfo> mediaCacheInfos) {
        long usrId = getUserId();
        if (mediaCacheInfos != null) {
            put(MY_MEDIA_CACHE_DATA + "_" + usrId, JSON.toJSONString(mediaCacheInfos));
        } else {
            put(MY_MEDIA_CACHE_DATA, "");
        }
    }

    public void saveMyMoments(List<MomentItemDto> momentItemDtos) {
        long userId = getUserId();
        if (momentItemDtos != null) {
            put(MY_MOMENT_LIST_DATA + "_" + userId, JSON.toJSONString(momentItemDtos));
        } else {
            put(MY_MOMENT_LIST_DATA + "_" + userId, "");
        }
    }


    public List<MomentItemDto> getMyMoments() {
        long userId = getUserId();

        String momentsJsonString = app.getString(MY_MOMENT_LIST_DATA + "_" + userId, "");
        if (!TextUtils.isEmpty(momentsJsonString)) {
            return JSON.parseArray(momentsJsonString, MomentItemDto.class);
        }
        return new ArrayList<>();
    }

    public void saveMoments(List<MomentItemDto> momentItemDtos) {
        if (momentItemDtos != null) {
            put(MOMENT_LIST_DATA, JSON.toJSONString(momentItemDtos));
        } else {
            put(MOMENT_LIST_DATA,"");
        }
    }
    public List<MomentItemDto> getMoments() {
       String jsonString = app.getString(MOMENT_LIST_DATA,"");
        if (!TextUtils.isEmpty(jsonString)) {
            return JSON.parseArray(jsonString,MomentItemDto.class);
        }
        return new ArrayList<>();
    }
    private void put(String key, Object value) {
        SharedPreferences.Editor editor = app.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        }

        editor.commit();
    }


    public void saveQiNiuToken(String qiNiuToken) {

    }
}
