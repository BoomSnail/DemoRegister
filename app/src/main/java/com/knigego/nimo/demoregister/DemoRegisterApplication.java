package com.knigego.nimo.demoregister;

import android.app.Application;
import android.content.Context;

import com.knigego.nimo.demoregister.storage.AppPref;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by ThinkPad on 2017/2/24.
 */

public class DemoRegisterApplication extends Application {
    private static DemoRegisterApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        ShareSDK.initSDK(this);
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static boolean isRole(String userRole) {
        if (AppPref.getInstance().getUserRole().equals("cc")) {
            return true;
        } else {
            return false;
        }
    }
}
