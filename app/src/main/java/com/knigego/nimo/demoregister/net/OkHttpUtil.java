
package com.knigego.nimo.demoregister.net;


import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;


/**
 *
 * Created by ThinkPad on 2017/2/23.
 */

public class OkHttpUtil {

    private static OkHttpClient singleton;


    private static final int TIME_CONNECT_OUT = 10 * 1000;
    private static final int TIME_READ_OUT = 40 * 1000;

    public static OkHttpClient getInstance() {
        if (singleton == null) {
            synchronized (OkHttpUtil.class) {
                if (singleton == null) {
                    singleton = new OkHttpClient();
                    singleton.setConnectTimeout(TIME_CONNECT_OUT,TimeUnit.MILLISECONDS);
                    singleton.setReadTimeout(TIME_READ_OUT,TimeUnit.MILLISECONDS);
                }
            }
        }
        return singleton;
    }
}
