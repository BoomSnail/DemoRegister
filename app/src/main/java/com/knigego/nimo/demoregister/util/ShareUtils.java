package com.knigego.nimo.demoregister.util;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.knigego.nimo.demoregister.AppConfig;
import com.knigego.nimo.demoregister.R;
import com.mob.tools.utils.UIHandler;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by ThinkPad on 2017/4/6.
 */

public class ShareUtils {

    private static String PATH_WELCOME = Environment.getExternalStorageDirectory().getPath() +
            AppConfig.PATH_IMAGE_DIR + "welcome.jpg";

    public static String getShareUrl(int type, long id) {
        return null;
    }

    /**
     * 分享给微信朋友
     *
     * @param context  上下文对象
     * @param title 标题
     * @param content  内容
     * @param shareUrl 分享链接
     */
    public static void shareWeChatFriend(Context context, String title, String content, String shareUrl, String imageUrl) {
        if (!CommonUtils.isAppInstalled(context,"com.tencent.mm")) {
            Toast.makeText(context, R.string.share_not_installed_wechat,Toast.LENGTH_SHORT).show();
            return;
        }
        //调用系统微信分享
//        Intent intent = new Intent();
//        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
//        intent.setComponent(comp);
//        intent.setAction(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_TEXT, getShareContent(context, nickName, content, shareUrl));
//        context.startActivity(intent);
        Wechat.ShareParams sp= new Wechat.ShareParams();
        sp.setTitle(title);
        sp.setText(content);
        sp.setUrl(shareUrl);
        sp.setImageUrl(imageUrl);
        sp.setShareType(Platform.SHARE_WEBPAGE);

        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(new ShareActionListener(context));
        wechat.share(sp);
    }

    /**
     * 分享到微信朋友圈
     *
     * @param context  上下文对象
     * @param title 标题
     * @param content  内容
     * @param shareUrl 分享链接
     */
    public static void shareWeChatCircle(Context context, String title, String content, String shareUrl, String imageUrl) {
        if(!CommonUtils.isAppInstalled(context,"com.tencent.mm")){
            Toast.makeText(context, R.string.share_not_installed_wechat,Toast.LENGTH_SHORT).show();
            return;
        }
//        Intent intent = new Intent();
//        ComponentName comp = new ComponentName("com.tencent.mm",
//                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
//        intent.setComponent(comp);
//        intent.setAction(Intent.ACTION_SEND);
//        intent.setType("image/*");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.putExtra("Kdescription", getShortShareContent(context, nickName, content, shareUrl));
//        intent.putExtra(Intent.EXTRA_STREAM, getShareUri(context));
//        context.startActivity(intent);
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setTitle(title);
        sp.setText(content);
        sp.setUrl(shareUrl);
        sp.setImageUrl(imageUrl);
        sp.setShareType(Platform.SHARE_WEBPAGE);

        Platform wechat = ShareSDK.getPlatform(WechatMoments.NAME);
        wechat.setPlatformActionListener(new ShareActionListener(context)); // 设置分享事件回调
        // 执行图文分享
        wechat.share(sp);
    }

    /**
     * 分享到facebook
     *
     * @param context  上下文对象
     * @param title 标题
     * @param content  内容
     * @param shareUrl 分享链接
     */
    public static void shareFacebook(Context context, String title, String content, String shareUrl, String imageUrl) {
        if(!CommonUtils.isAppInstalled(context,"com.facebook.katana")){
            Toast.makeText(context, R.string.share_not_installed_facebook,Toast.LENGTH_SHORT).show();
            return;
        }
//        Intent intent = new Intent();
//        ComponentName comp = new ComponentName("com.facebook.katana", "com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias");
//        intent.setComponent(comp);
//        intent.setAction(Intent.ACTION_SEND);
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
//        intent.putExtra(Intent.EXTRA_STREAM, getShareUri(context));
//        context.startActivity(intent);
        Facebook.ShareParams sp = new Facebook.ShareParams();
        sp.setText(getShareContent(title, content, shareUrl));
        sp.setImageUrl(imageUrl);
//        sp.setShareType(Platform.SHARE_WEBPAGE);

        Platform facebook = ShareSDK.getPlatform(Facebook.NAME);
        facebook.setPlatformActionListener(new ShareActionListener(context)); // 设置分享事件回调
        // 执行图文分享
        facebook.share(sp);
    }

    private static String getShareContent(String title, String content, String shareUrl) {
        return null;
    }
    /**
     * 分享到微博
     *
     * @param context  上下文对象
     * @param title 标题
     * @param content  内容
     * @param shareUrl 分享链接
     */
    public static void shareWeibo(Context context, String title, String content, String shareUrl, String imageUrl) {
        if(!CommonUtils.isAppInstalled(context,"com.sina.weibo")){
            Toast.makeText(context, R.string.share_not_installed_weibo,Toast.LENGTH_SHORT).show();

            return;
        }
        //调用系统新浪微博分享
//        Intent intent = new Intent();
//        ComponentName comp = new ComponentName("com.sina.weibo", "com.sina.weibo.composerinde.ComposerDispatchActivity");
//        intent.setComponent(comp);
//        intent.setAction(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_TEXT, getShortShareContent(context, nickName, content, shareUrl));
//        context.startActivity(intent);

        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setText(getShortShareContent(title, content, shareUrl));
        if(!TextUtils.isEmpty(imageUrl)){
            sp.setImageUrl(imageUrl);
            sp.setShareType(Platform.SHARE_IMAGE);
        }else {
            sp.setShareType(Platform.SHARE_TEXT);
        }

        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new ShareActionListener(context)); // 设置分享事件回调
        // 执行图文分享
        weibo.share(sp);
    }

    private static String getShortShareContent(String title, String content, String shareUrl) {
        return null;
    }

    public static void shareQQFriend(Context context, String title, String content, String shareUrl, String imageUrl) {
        if(!CommonUtils.isAppInstalled(context,"com.tencent.mobileqq")){
            Toast.makeText(context, R.string.share_not_installed_qq,Toast.LENGTH_SHORT).show();
            return;
        }
//        Intent intent = new Intent();
//        ComponentName comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
//        intent.setComponent(comp);
//        intent.setAction(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_TEXT, getShortShareContent(context, nickName, content, shareUrl));
//        context.startActivity(intent);
        QQ.ShareParams sp = new QQ.ShareParams();
        if (TextUtils.isEmpty(title)) {
            sp.setText(title);
        } else {
            sp.setTitle(title);
            sp.setText(content);
        }
        sp.setTitleUrl(shareUrl);
        sp.setImageUrl(imageUrl);

        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(new ShareActionListener(context)); // 设置分享事件回调
        // 执行图文分享
        qq.share(sp);
    }


    public static class ShareActionListener implements PlatformActionListener, Handler.Callback {
        private Context mContext;
        public ShareActionListener(Context context){
            mContext = context;
        }
        public void onComplete(Platform plat, int action,
                               HashMap<String, Object> res) {

            Message msg = new Message();
            msg.arg1 = 1;
            msg.arg2 = action;
            msg.obj = plat;
            UIHandler.sendMessage(msg, this);
        }

        public void onCancel(Platform palt, int action) {
            Message msg = new Message();
            msg.arg1 = 3;
            msg.arg2 = action;
            msg.obj = palt;
            UIHandler.sendMessage(msg, this);
        }

        public void onError(Platform palt, int action, Throwable t) {
            t.printStackTrace();
            Message msg = new Message();
            msg.arg1 = 2;
            msg.arg2 = action;
            msg.obj = t.getMessage();
            UIHandler.sendMessage(msg, this);

        }

        public boolean handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1:
                    // 成功
                    Toast.makeText(mContext,R.string.share_success,Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    // 失败
                    Toast.makeText(mContext,R.string.share_fail,Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    // 取消
                    Toast.makeText(mContext,R.string.share_dance,Toast.LENGTH_SHORT).show();

                    break;
            }

            return false;
        }
    }
}
