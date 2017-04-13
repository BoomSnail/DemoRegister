package com.knigego.nimo.demoregister.util;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.knigego.nimo.demoregister.service.AudioService;
import com.knigego.nimo.demoregister.ui.activities.StageDetailActivity;

/**
 * Created by ThinkPad on 2017/4/5.
 */

public class AudioManager {
    private LinearLayout mAudioPlayLayout;
    private ImageView mImageView;
    private TextView mProgressTimeText;
    private TextView mTotalTimeText;
    private SeekBar mSeekBarProgress;

    private static final int HANDLER_AUDIO_PLAY_PROGRESS = 3;
    private boolean mBound = false;
    private boolean playStatus = false;
    private AudioService mAudioService;

    private Thread mThread;

    //设置播放路径
    public void setAudioPath(String audioPath) {
        mAudioPath = audioPath;
    }

    private String mAudioPath;

    public AudioManager() {
    }

    public void bindService(Context context) {

    }

    public void pause() {
    }

    public boolean isPlayStatus() {
        return false;
    }

    public void destroy(StageDetailActivity stageDetailActivity) {
    }
}
