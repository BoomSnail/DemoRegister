package com.knigego.nimo.demoregister.util;

import android.content.Context;
import android.hardware.camera2.params.Face;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.knigego.nimo.demoregister.AppConfig;
import com.knigego.nimo.demoregister.ui.activities.SettingActivity;

import java.io.File;

/**
 *
 * Created by ThinkPad on 2017/4/14.
 */

public class DataCleanManager {

    private static final String SD_VIDEO_CACHE = AppConfig.getDemoRegisterFile().getPath() + AppConfig.PATH_VIDEO;
    private static final String SD_VIDEO_IMAGE_CACHE = AppConfig.getDemoRegisterFile().getPath() + AppConfig.PATH_VIDEO_IMAGE;
    private static final String SD_APP_UPDATE_CACHE = AppConfig.getDemoRegisterFile().getPath() + AppConfig.PATH_UPDATE;
    private static final String SD_AUDIO_DOWNLOAD_CACHE = AppConfig.getDemoRegisterFile().getPath() + AppConfig.PATH_AUDIO_DOWNLOAD;
    private static final String SD_DCIM_CACHE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + AppConfig.ALBUM_NAME;

    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());//清除本应用的系统缓存
        if (Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
            File videoFile = new File(SD_VIDEO_CACHE);
            if (videoFile.exists()) {
                deleteDir(videoFile);
            }
            File videoImageFile = new File(SD_VIDEO_IMAGE_CACHE);
            if (videoImageFile.exists()) {
                deleteDir(videoImageFile);
            }

            File appUpdateFile = new File(SD_APP_UPDATE_CACHE);
            if (appUpdateFile.exists()) {
                deleteDir(appUpdateFile);
            }

            File audioDownloadFile = new File(SD_AUDIO_DOWNLOAD_CACHE);
            if (audioDownloadFile.exists()) {
                deleteDir(audioDownloadFile);
            }

            File tuTuFile = new File(SD_DCIM_CACHE);
            if (tuTuFile.exists()) {
                deleteDir(tuTuFile);
            }
            File glideFile = Glide.getPhotoCacheDir(context);
            if (glideFile != null && glideFile.exists()) {
                deleteDir(glideFile);
            }
        }

    }

    private static boolean deleteDir(File cacheDir) {
        try {
            if (cacheDir != null && cacheDir.isDirectory()) {
                String[] children = cacheDir.list();
                if (children == null) {
                    return false;
                }
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(cacheDir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            return cacheDir.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
