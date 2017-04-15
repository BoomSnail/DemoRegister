package com.knigego.nimo.demoregister.util;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.crooods.wd.dto.post.MediaImageDto;
import com.knigego.nimo.demoregister.ui.activities.SettingActivity;

import java.util.List;

/**
 * Created by ThinkPad on 2017/3/29.
 */

public class CommonUtils {
    public static String[] getPictures(List<MediaImageDto> mediaImageDtos) {
        if (mediaImageDtos != null) {
            String[] images = new String[mediaImageDtos.size()];
            for (int i = 0; i < mediaImageDtos.size(); i++) {
                images[i] = mediaImageDtos.get(i).getUrl();
            }
            return images;
        }
        return null;
    }

    /**
     * 文本复制
     */
    public static void copy(String content, Context context) {
        //获取剪贴板管理器
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(content);
    }

    /**
     * 实现粘贴
     */
    public static String paste(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return clipboardManager.getText().toString().trim();
    }

    /**
     * 弹起软键盘
     */
    public static void showSoftInputMethod(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘，界面无焦点
     */
    public static void hideSoftInput(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view.getWindowToken() != null) {
            boolean b = inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * 检测应用是否安装
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName,0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }


    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            versionName = packageInfo.versionName;
            if (versionName == null) {
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    public static boolean isEmail(String account) {
        return false;
    }
}
