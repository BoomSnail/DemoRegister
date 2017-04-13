package com.knigego.nimo.demoregister;

import android.os.Environment;

import java.io.File;

/**
 * Created by ThinkPad on 2017/4/2.
 */

public class AppConfig {

    /**音频下载保存路径*/
    public static final String PATH_AUDIO_DOWNLOAD = "/DemoRegister/AudioDownload/";
    /**视频下载保存路径*/
    public static final String PATH_VIDEO_DOWNLOAD = "/DemoRegister/VideoDownload/";
    /***请求路径*/
    public static final String APP_PATH = "http://app.topshow.cc:8888";
    /**内容发布协议*/
    public static final String PATH_PUBLISH = APP_PATH + "/v1.0/static/publish.html";
    public static final String QINIU_DOMAIN = "7xlg68.com2.z0.glb.qiniucdn.com";
    public static final String PATH_VIDEO_IMAGE = "/DemoRegister/VideoImages/" ;
    public static final String PATH_IMAGE_DIR = "/DemoRegister/Images/";

    public static File getDemoRegisterFile(){
        return Environment.getExternalStorageDirectory();
    }
}
