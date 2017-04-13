package com.knigego.nimo.demoregister.net;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.client.FastJSONConverter;
import com.crooods.wd.service.IUserStubService;
import com.knigego.nimo.demoregister.AppConfig;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.activities.LoginActivity;

import java.lang.ref.WeakReference;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


/**
 * retrofit&okHttp
 * Created by knife_nimo on 2017/2/23.
 */

public class RetrofitUtil {

    private static RestAdapter sRetrofit;

    public static <T> T createApi(Class<T> clz) {
        if (sRetrofit == null) {
            synchronized (RetrofitUtil.class) {
                if (sRetrofit == null) {
                    RestAdapter.Builder builder = new RestAdapter.Builder();
                    builder.setEndpoint(AppConfig.APP_PATH);
                    builder.setConverter(new FastJSONConverter());
                    builder.setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addHeader("Accept", "application/json");
                            request.addQueryParam("debug", "true");
                        }
                    });
                    builder.setClient(new OkClient(OkHttpUtil.getInstance()));
                    builder.setLogLevel(true ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE);
                    sRetrofit = builder.build();

                }
            }

        }
        return sRetrofit.create(clz);
    }

    public static class ActivityCallback<T> implements Callback<T> {
        private final WeakReference<Context> mRef;

        public ActivityCallback(Context context) {
            mRef = new WeakReference<Context>(context);
        }

        public Context getContext() {
            return mRef.get();
        }


        @Override
        public void success(T t, Response response) {
            ResponseT responseT = (ResponseT) t;
            String responseCode = responseT.getRtnCode();
            if (!responseCode.equals(AppConstants.SUCCESS)) {//加载失败
                if (responseCode.equals(AppConstants.TOKEN_INVALID)
                        || responseCode.equals(AppConstants.TOKEN_NULL)
                        || responseCode.equals(AppConstants.TOKEN_EXPIRED)
                        || responseCode.equals(AppConstants.TOKEN_ERROR)) {
                    obtainAccessToken(getContext());
                } else {
                    String msg = responseT.getMsg();
                    if (!TextUtils.isEmpty(msg)) {
                        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }

        @Override
        public void failure(RetrofitError error) {
            if (error.getResponse() == null) {
                return;
            }
            if (mRef == null || mRef.get() == null) {
                return;
            }
            int errorCode = error.getResponse().getStatus();
            if (errorCode == 401) {
                if (!TextUtils.isEmpty(AppPref.getInstance().getAccessToken())) {
                    obtainAccessToken(getContext());
                } else {
                    if (mRef.get() instanceof LoginActivity) {
                        Toast.makeText(mRef.get(), "用户名不存在", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(mRef.get(), "请先登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mRef.get(),LoginActivity.class);
                        mRef.get().startActivity(intent);
                    }
                }
            } else if (errorCode == 400) {
                if (mRef.get() instanceof LoginActivity) {
                    Toast.makeText(mRef.get(),"用户名或者密码错误",Toast.LENGTH_SHORT).show();
                }

            }
        }

        private void obtainAccessToken(final Context context) {

            final String username = AppPref.getInstance().getLoginName();
            String password = AppPref.getInstance().getPassword();
            IUserStubService iUserStubService = createApi(IUserStubService.class);
            iUserStubService.obtainAccessToken("mobile-client", "mobile", "password", "read,write", username, password ,
                    new ActivityCallback<ResponseT<JSONObject>>(context){
                        @Override
                        public void success(ResponseT<JSONObject> jsonObjectResponseT, Response response) {
                            if (jsonObjectResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                                String accessToken = jsonObjectResponseT.getBizData().getString("value");
                                if (!TextUtils.isEmpty(accessToken)) {
                                    AppPref.getInstance().saveAccessToken(accessToken);
                                }
                            } else {
                                Toast.makeText(context,jsonObjectResponseT.getMsg(),Toast.LENGTH_SHORT).show();
                                AppPref.getInstance().saveAccessToken("");
                                if (context instanceof LoginActivity) {
                                    return;
                                } else {
                                    Intent intent = new Intent(context,LoginActivity.class);
                                    context.startActivity(intent);
                                }

                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            super.failure(error);
                        }
                    });

        }
    }
}