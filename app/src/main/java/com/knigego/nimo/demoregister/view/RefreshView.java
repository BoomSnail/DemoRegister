package com.knigego.nimo.demoregister.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.canyinghao.canrefresh.CanRefresh;
import com.knigego.nimo.demoregister.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 *
 * Created by ThinkPad on 2017/3/28.
 */

public class RefreshView extends FrameLayout implements CanRefresh {

    public static final int TYPE_STAGE = 0;//舞台
    public static final int TYPE_MOMENT = 1;//动态
    private int mType = TYPE_STAGE;
    private GifImageView mGifImageView;
    private int[] mPullInts;

    public RefreshView(@NonNull Context context) {
        this(context,null);
    }

    public RefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public RefreshView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RefreshView,defStyleAttr,0);
        mType = typedArray.getInt(R.styleable.RefreshView_refresh_type,TYPE_STAGE);
        typedArray.recycle();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_refresh,null);
        addView(view);
        if (mType == TYPE_STAGE) {
            mPullInts = new int[]{
                    R.drawable.exercise_pull_in00, R.drawable.exercise_pull_in01,
                    R.drawable.exercise_pull_in02, R.drawable.exercise_pull_in03,
                    R.drawable.exercise_pull_in04, R.drawable.exercise_pull_in05,
                    R.drawable.exercise_pull_in06, R.drawable.exercise_pull_in07,
                    R.drawable.exercise_pull_in08, R.drawable.exercise_pull_in09,
                    R.drawable.exercise_pull_in10, R.drawable.exercise_pull_in11,
                    R.drawable.exercise_pull_in12, R.drawable.exercise_pull_in13,
                    R.drawable.exercise_pull_in14, R.drawable.exercise_pull_in15,
                    R.drawable.exercise_pull_in16, R.drawable.exercise_pull_in17,
                    R.drawable.exercise_pull_in18, R.drawable.exercise_pull_in19,
                    R.drawable.exercise_pull_in20, R.drawable.exercise_pull_in21,
                    R.drawable.exercise_pull_in22, R.drawable.exercise_pull_in23,
                    R.drawable.exercise_pull_in24

            };
        } else {
            mPullInts = new int[]{
                    R.drawable.find_pull_in00,R.drawable.find_pull_in01,
                    R.drawable.find_pull_in02,R.drawable.find_pull_in03,
                    R.drawable.find_pull_in04,R.drawable.find_pull_in05,
                    R.drawable.find_pull_in06,R.drawable.find_pull_in07,
                    R.drawable.find_pull_in08,R.drawable.find_pull_in09,
                    R.drawable.find_pull_in10,R.drawable.find_pull_in11,
                    R.drawable.find_pull_in12,R.drawable.find_pull_in13,
                    R.drawable.find_pull_in14,R.drawable.find_pull_in15,
                    R.drawable.find_pull_in16,R.drawable.find_pull_in17,
                    R.drawable.find_pull_in18,R.drawable.find_pull_in19,
                    R.drawable.find_pull_in20,R.drawable.find_pull_in21,
                    R.drawable.find_pull_in22,R.drawable.find_pull_in23,
                    R.drawable.find_pull_in24
            };
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mGifImageView = (GifImageView) findViewById(R.id.image_loading);
    }

    @Override
    public void onReset() {
        mGifImageView.setVisibility(GONE);
    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onRelease() {//放手后
        mGifImageView.setVisibility(VISIBLE);
        int resId = R.drawable.loading_stage;
        if (mType == TYPE_MOMENT) {
            resId = R.drawable.loading_moment;
        }
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(),resId);
            mGifImageView.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete() {//刷新完成
        mGifImageView.setVisibility(VISIBLE);
        int resId = R.drawable.loading_stage_out;
        if (mType == TYPE_MOMENT) {
            resId = R.drawable.loading_moment_out;
        }

        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(),resId);
            mGifImageView.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPositionChange(float currentPercent) {//下拉高度与头部高度的比例

        mGifImageView.setVisibility(VISIBLE);
        int position = Math.round(currentPercent * 25);
        if (position > 25) {
            position = 25;
        }
        if (position < 1) {
            position = 1;
        }

        int loadingResId = mPullInts[position - 1];
        mGifImageView.setImageResource(loadingResId);
    }

    @Override
    public void setIsHeaderOrFooter(boolean isHeader) {

    }
}
