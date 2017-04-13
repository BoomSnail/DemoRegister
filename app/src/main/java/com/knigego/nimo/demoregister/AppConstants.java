package com.knigego.nimo.demoregister;

/**
 * 常量
 * Created by ThinkPad on 2017/3/27.
 */

public class AppConstants {

    /**请求成功*/
    public static final String SUCCESS = "0000000";
    /**token失效/非法*/
    public static final String TOKEN_INVALID = "00210001";
    /**token ===  null**/
    public static final String TOKEN_NULL = "00210002";
    /**token过期*/
    public static final String TOKEN_EXPIRED = "00210003";
    /**token验证失败*/
    public static final String TOKEN_ERROR = "00210004";



    /**舞台*/

    public static final int TYPE_STAGE = 0;
    /**动态*/
    public static final int TYPE_MOMENT = 1;
    /**
     * 舞台更新
     */
    public static final String ACTION_STAGE_UPDATE = "action_stage_update";
    /**动态更新*/
    public static final String ACTION_MOMENT_UPDATE = "action_moment_update";
    /**舞台删除*/
    public static final String ACTION_STAGE_DELETE = "action_moment_delete";
    /**动态删除*/
    public static final String ACTION_MOMENT_DELETE = "action_moment_delete";
    /**用户信息更新*/
    public static final String ACTION_USER_INFO_UPDATE = "action_user_info_update";
    /**更新聊天列表*/
    public static final String ACTION_CHAT_LIST_UPDATE = "action_chat_list_update";
    /**消息更新通知*/
    public static final String ACTION_MESSAGE_UPDATE = "action_message_update";


    public static final String STAGE_ITEM = "STAGE_ITEM";
    public static final String IS_COMMENT = "IS_COMMENT";
    public static final String TYPE = "TYPE" ;
    public static final String RELEASE_CONTENT_KEY = "RELEASE_CONTENT_KEY";
}
