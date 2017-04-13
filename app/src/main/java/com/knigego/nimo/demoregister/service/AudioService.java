package com.knigego.nimo.demoregister.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 *
 * Created by ThinkPad on 2017/4/5.
 */

public class AudioService extends Service {

    private IBinder mMusicBind = new MyBinder();
    //获取到activity的Handler，用来通知更新进度条
    Handler mHandler;
    MediaPlayer mMediaPlayer;    //播放音乐的媒体类

    private  boolean isPlayComplete = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBind;
    }

    public class MyBinder extends Binder{
        public Service getService(){
            return AudioService.this;
        }
    }
}
