package com.knigego.nimo.demoregister.util;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by ThinkPad on 2017/4/5.
 */

public class AndroidBug5497Workaround {
    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
    public static void assistActivity(Activity activity) {
        new AndroidBug5497Workaround(activity,null);
    }

    public static void assistActivity(Activity activity,OnKeyboardChangeListener onKeyboardChangeListener) {
        new AndroidBug5497Workaround(activity,onKeyboardChangeListener);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private Activity mActivity;
    private OnKeyboardChangeListener mOnKeyboardChangeListener;

    private AndroidBug5497Workaround(Activity activity,OnKeyboardChangeListener onKeyboardChangeListener) {
        mActivity = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
        mOnKeyboardChangeListener = onKeyboardChangeListener;
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                int v = mActivity.getWindow().getAttributes().flags;
                // 全屏 66816 - 非全屏 65792
                if(v != 66816){
                    int statusBarHeight = getStatusBarHeight(mActivity);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                    }else {
                        // keyboard probably just became visible
//                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference + statusBarHeight;

                        frameLayoutParams.height = usableHeightSansKeyboard;
                    }
                }else{
                    // keyboard probably just became visible
                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                }
                if(mOnKeyboardChangeListener!=null){
                    mOnKeyboardChangeListener.onKeyboardShow();
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    int statusBarHeight = getStatusBarHeight(mActivity);
                    frameLayoutParams.height = usableHeightSansKeyboard - statusBarHeight;
                }else {
                    // keyboard probably just became hidden
                    frameLayoutParams.height = usableHeightSansKeyboard;
                }
                if(mOnKeyboardChangeListener!=null){
                    mOnKeyboardChangeListener.onKeyboardHide();
                }
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    private int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public interface OnKeyboardChangeListener{
        /**键盘弹起*/
        void onKeyboardShow();
        /**键盘收起*/
        void onKeyboardHide();
    }
}
