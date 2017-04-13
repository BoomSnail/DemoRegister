package com.knigego.nimo.demoregister.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.knigego.nimo.demoregister.R;

import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * 自定义视频播放
 * Created by ThinkPad on 2017/3/31.
 */

public class CustomMediaController extends MediaController {
    private GestureDetector mGestureDetector;
    private ImageButton image_back;//返回按钮
    private TextView mFileName;//文件名
    private VideoView mVideoView;

    private Activity mActivity;
    private Context mContext;
    private String videoName;//视频名称

    private int controllerWith = 0;//

    private View mVollumerBrighnessLayout;//提示窗口
    private ImageView mOperationBg;//提示图片
    private TextView mOperationTv;//提示文字

    private AudioManager mAudioManager;

    //最大声音
    private int mMaxVolume;
    //当前声音
    private int mCurrentVolume = -1;
    //当前亮度
    private float mCurrentBrightness = -1f;

    //隐藏提示窗口
    public static final int HIDEFRAM = 0;


    public CustomMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomMediaController(Context context) {
        super(context);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDEFRAM:
                    mVollumerBrighnessLayout.setVisibility(GONE);
                    mOperationTv.setVisibility(GONE);
                    break;
            }
        }
    };

    private ImageView mIvScale;

    public CustomMediaController(Context context, VideoView videoView, Activity activity) {
        super(context);
        this.mContext = context;
        this.mVideoView = videoView;
        this.mActivity = activity;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        controllerWith = windowManager.getDefaultDisplay().getWidth();
        mGestureDetector = new GestureDetector(context, new MyGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        //处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return true;
    }

    private void endGesture() {
        mCurrentVolume = -1;
        mCurrentBrightness = -1;
        //隐藏
        mHandler.removeMessages(HIDEFRAM);
        mHandler.sendEmptyMessageDelayed(HIDEFRAM, 1);
    }

    //获取自定义布局文件
    @Override
    protected View makeControllerView() {
        View controllerView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(getResources().getIdentifier("mymediacontroller", "layout", getContext().getPackageName()), this);
        controllerView.setMinimumHeight(controllerWith);
        //获取控件
        image_back = (ImageButton) controllerView.findViewById(R.id.mediacontroller_top_back);
        mFileName = (TextView) controllerView.findViewById(R.id.mediacontroller_filename);
        mIvScale = (ImageView) controllerView.findViewById(R.id.mediacontroller_scale);

        if (mFileName != null) {
            mFileName.setText(videoName);
        }
        //声音控制
        mVollumerBrighnessLayout = controllerView.findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) controllerView.findViewById(R.id.operation_bg);
        mOperationTv = (TextView) controllerView.findViewById(R.id.operation_tv);
        mOperationTv.setVisibility(GONE);

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        //最大声音
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //返回监听
        image_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivity != null) {
                    mActivity.finish();
                }
            }
        });
        mIvScale.setOnClickListener(new OnClickListener() { //横竖屏切换问题
            @Override
            public void onClick(View v) {
                if (mActivity != null) {
                    switch (mActivity.getResources().getConfiguration().orientation) {
                        case Configuration.ORIENTATION_LANDSCAPE://横屏
                            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            break;
                        case Configuration.ORIENTATION_PORTRAIT:
                            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            break;
                    }
                }
            }
        });
        return controllerView;
    }


    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        /**
         * 因为使用的是自定义的mediaController 当显示后，mediaController会铺满屏幕，
         * 所以VideoView的点击事件会被拦截，所以重写控制器的手势事件，
         * 将全部的操作全部写在控制器中，
         * 因为点击事件被控制器拦截，无法传递到下层的VideoView，
         * 所以 原来的单机隐藏会失效，作为代替，
         * 在手势监听中onSingleTapConfirmed（）添加自定义的隐藏/显示，
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //手势结束后，并且是单机结束后，控制器显示隐藏
            toggleMediaControlsVisibility();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            int x = (int) e1.getRawX();

            Display display = mActivity.getWindowManager().getDefaultDisplay();
            int windowWith = display.getWidth();
            int windowHeight = display.getHeight();
            if (mOldX > windowWith * 3.0 / 4.0) {//右边滑动
                onVolumeSlide((mOldY - y) / windowHeight);
            } else if (mOldX < windowWith * 1.0 / 4.0) {//左边滑动
                onBrightnessSlide((mOldY - y) / windowHeight);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            playOrPause();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private void playOrPause() {
        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
            } else {
                mVideoView.start();
            }
        }
    }

    private void toggleMediaControlsVisibility() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }

    private void onVolumeSlide(float v) {
        if (mCurrentVolume == -1) {
            mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mCurrentVolume < 0) {
                mCurrentVolume = 0;
            }
                mVollumerBrighnessLayout.setVisibility(VISIBLE);
                mOperationTv.setVisibility(VISIBLE);

        }
        int index = (int) (v * mMaxVolume) + mCurrentVolume;
        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0 ) {
            index = 0;
        }
        if (index >= 10) {
            mOperationBg.setImageResource(R.drawable.volmn_100);
        } else if (index >= 5 && index < 10) {
            mOperationBg.setImageResource(R.drawable.volmn_60);
        } else if (index > 0 && index < 5) {
            mOperationBg.setImageResource(R.drawable.volmn_30);
        } else {
            mOperationBg.setImageResource(R.drawable.volmn_no);
        }
        mOperationTv.setText((int) (((double) index / mMaxVolume) * 100) + "%");
        //变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    }


    private void onBrightnessSlide(float v) {
        if (mCurrentBrightness < 0) {
            mCurrentBrightness = mActivity.getWindow().getAttributes().screenBrightness;
            if (mCurrentBrightness <= 0.00f) {
                mCurrentBrightness = 0.50f;
            }
            if (mCurrentBrightness < 0.01f) {
                mCurrentBrightness = 0.01f;
            }

            mVollumerBrighnessLayout.setVisibility(VISIBLE);
            mOperationTv.setVisibility(VISIBLE);

            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            lp.screenBrightness = mCurrentBrightness + v;
            if (lp.screenBrightness > 1.0f) {
                lp.screenBrightness = 1.0f;
            } else if (lp.screenBrightness < 0.01f) {
                lp.screenBrightness = 0.01f;
            }
            mActivity.getWindow().setAttributes(lp);
            mOperationTv.setText((int) (lp.screenBrightness * 100) + "%");

            if (lp.screenBrightness * 100 >= 90) {
                mOperationBg.setImageResource(R.drawable.light_100);
            } else if (lp.screenBrightness * 100 >= 80 && lp.screenBrightness * 100 < 90) {
                mOperationBg.setImageResource(R.drawable.light_90);
            } else if (lp.screenBrightness * 100 >= 70 && lp.screenBrightness * 100 < 80) {
                mOperationBg.setImageResource(R.drawable.light_80);
            } else if (lp.screenBrightness * 100 >= 60 && lp.screenBrightness * 100 < 70) {
                mOperationBg.setImageResource(R.drawable.light_70);
            } else if (lp.screenBrightness * 100 >= 50 && lp.screenBrightness * 100 < 60) {
                mOperationBg.setImageResource(R.drawable.light_60);
            } else if (lp.screenBrightness * 100 >= 40 && lp.screenBrightness * 100 < 50) {
                mOperationBg.setImageResource(R.drawable.light_50);
            } else if (lp.screenBrightness * 100 >= 30 && lp.screenBrightness * 100 < 40) {
                mOperationBg.setImageResource(R.drawable.light_40);
            } else if (lp.screenBrightness * 100 >= 20 && lp.screenBrightness * 100 < 20) {
                mOperationBg.setImageResource(R.drawable.light_30);
            } else if (lp.screenBrightness * 100 >= 10 && lp.screenBrightness * 100 < 20) {
                mOperationBg.setImageResource(R.drawable.light_20);
            }

        }
    }

    public void setVideoName(String name) {
        videoName = name;
        if (mFileName != null) {
            mFileName.setText(videoName);
        }
    }
}

