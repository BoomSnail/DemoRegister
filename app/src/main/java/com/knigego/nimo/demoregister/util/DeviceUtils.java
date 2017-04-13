package com.knigego.nimo.demoregister.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by ThinkPad on 2017/3/30.
 */

public class DeviceUtils {

    public static int dipToPX(final Context ctx, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, ctx.getResources().getDisplayMetrics());
    }

    /**屏幕宽度*/
    public static int getScreenWidth(Context context) {
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }
    /**屏幕高度*/
    public static int getScreenHeight(Context context){
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getHeight();
    }
}
