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
    /**视频*/
    public static final String PATH_VIDEO = "/DemoRegister/Videos/";
    /**视频图片*/
    public static final String PATH_VIDEO_IMAGE = "/DemoRegister/VideoImages/" ;
    public static final String PATH_IMAGE_DIR = "/DemoRegister/Images/";
    //服务条款
    public static final String PATH_AGREEMENT = APP_PATH + "/v1.0/static/agreement.html";
    public static final String PATH_PRIVACY = APP_PATH+"/v1.0/static/privacy.html";
    /**apk更新保存*/
    public static final String PATH_UPDATE ="/DemoRegister/Update/" ;
    /**保存到文件夹名称*/
    public static final String ALBUM_NAME = "DemoRegister";

    public static File getDemoRegisterFile(){
        return Environment.getExternalStorageDirectory();
    }
}
