package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.util.CustomMediaController;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayActivity extends BaseActivity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    @Bind(R.id.buffer)
    VideoView mVideoView;
    @Bind(R.id.probar)
    ProgressBar mProbar;
    @Bind(R.id.download_rate)
    TextView mDownloadRate;
    @Bind(R.id.load_rate)
    TextView mLoadRate;

    private Uri mUri;
    private String videoName = "正在播放...";
    private CustomMediaController mMediaController;
    private String VideoUrl;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        //设置全屏
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = getWindow();
        window.setFlags(flag, flag);
        //初始化
        Vitamio.isInitialized(this);

        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);
        hideToolbar(false);
        getIntentValue();
        initView();
        initData();
    }

    private void initData() {
        mUri = Uri.parse(VideoUrl);
        mVideoView.setVideoURI(mUri);//设置视频播放地址
        Log.i(" dsfsf----", "initData: " + VideoUrl);
        Log.i(" dkfjlskjdfl--------", "initData: " + mUri);
        mMediaController.show(5000);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
        mVideoView.requestFocus();
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setPlaybackSpeed(1.0f);
            }
        });
    }

    private void initView() {
        mMediaController = new CustomMediaController(this, mVideoView, this);
        mMediaController.setVideoName(videoName);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mLoadRate.setText(percent + "%");
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mProbar.setVisibility(View.VISIBLE);
                    mDownloadRate.setText("");
                    mLoadRate.setText("");
                    mDownloadRate.setVisibility(View.VISIBLE);
                    mLoadRate.setVisibility(View.VISIBLE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                mProbar.setVisibility(View.GONE);
                mDownloadRate.setVisibility(View.GONE);
                mLoadRate.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                mDownloadRate.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }

    public void getIntentValue() {
        Intent intent = getIntent();
        VideoUrl = intent.getStringExtra("videoUrl");
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mVideoView != null) {
//            mVideoView.stopPlayback();
//        }
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mVideoView.isPlaying()) {
//            mVideoView.pause();
//        }
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null) {
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        }
        super.onConfigurationChanged(newConfig);
    }
}
