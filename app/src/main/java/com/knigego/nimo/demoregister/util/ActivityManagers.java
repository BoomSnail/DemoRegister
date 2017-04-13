package com.knigego.nimo.demoregister.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * activity管理类
 * Created by ThinkPad on 2017/4/8.
 */

public class ActivityManagers {
    private static Stack<Activity> sActivities = new Stack<>();

    private static ActivityManagers sManager;

    public static ActivityManagers getManager() {
        if (sManager == null) {
            synchronized (ActivityManagers.class){
                if (sManager == null) {
                    sManager = new ActivityManagers();
                }
            }
        }
        return sManager;
    }
    /**添加activity*/
    public  void addActivity(Activity activity){
        if (activity != null) {
            sActivities.add(activity);
        }
    }

    /** get current activity*/
    public  Activity  getCurrentActivity(){

        return sActivities.lastElement();
    }

    /**finish current activity*/
    public  void finishCurrentActivity(){
        if (sActivities != null) {
            Activity activity =  sActivities.pop();
            activity .finish();
        }
    }

    /**finish the spe activity**/
    public  void  finishActivity(Activity activity){
        if (activity != null) {
            sActivities.remove(activity);
            if (activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**finish the spe class activity*/
    public  void finishActivity(Class<?> cls){
        for (Activity activity:sActivities){
            if (activity.getClass().equals(cls)) {
                activity.finish();
            }
        }
    }
    /**finish all activity*/
    public  void finishAllActivity(){
        for (Activity activity : sActivities){
            if (activity != null) {
                activity.finish();
            }
        }
        sActivities.clear();
    }

    /**exit application*/
    public  void AppAplicationExit(Context context){
        try {
            finishAllActivity();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
