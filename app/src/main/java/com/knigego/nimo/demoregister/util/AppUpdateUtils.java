package com.knigego.nimo.demoregister.util;

/**
 * Created by ThinkPad on 2017/4/14.
 */

public class AppUpdateUtils {
    /**
     * 使用蒲公英sdk检测更新
     * @param activity 上下文对象
     * @param isSilent 是否静默检测
     */
//    public static void checkPgyUpdate(final Activity activity, final boolean isSilent) {
//        if (!isSilent) {
//            Toast.makeText(activity, R.string.checking_version,Toast.LENGTH_SHORT).show();
//        }
//        PgyUpdateManager.register(activity, new UpdateManagerListener() {
//            @Override
//            public void onNoUpdateAvailable() {
//                if (!isSilent) {
//                    Toast.makeText(activity,R.string.app_is_no_update,Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onUpdateAvailable(String s) {
//                if (activity == null) {
//                    return;
//                }
//
//                final AppBean appBean = getAppBeanFromString(s);
//                new AlertDialog.Builder(activity)
//                        .setTitle(R.string.find_new_version)
//                        .setMessage(appBean.getReleaseNote())
//                        .setPositiveButton(R.string.now_update, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                startDownloadTask(activity,appBean.getDownloadURL());
//                                dialog.dismiss();
//                            }
//                        })
//                        .setNegativeButton(R.string.later_update, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).create().show();
//            }
//        });
//    }
}
