package com.knigego.nimo.demoregister.util;

import android.content.Context;

import com.crooods.common.protocol.ResponseT;
import com.crooods.wd.dto.qiniu.QiniuTokenDto;
import com.crooods.wd.service.IQiNiuStubService;
import com.knigego.nimo.demoregister.AppConfig;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 七牛云存储
 * Created by ThinkPad on 2017/4/9.
 */

public class QiNiuUtils {
    private static UploadManager sUploadManager;

    public static void upLoad(Context context, String postType, String mediaType, final String filePath, final UpCompletionHandler upCompletionHandler, final UpCancellationSignal upCancellationSignal) {
        IQiNiuStubService stubService = RetrofitUtil.createApi(IQiNiuStubService.class);
        stubService.qiniuAccessToken(AppPref.getInstance().getAccessToken(), postType, mediaType, new RetrofitUtil.ActivityCallback<ResponseT<QiniuTokenDto>>(context) {
            @Override
            public void success(ResponseT<QiniuTokenDto> qiniuTokenDtoResponseT, Response response) {
                super.success(qiniuTokenDtoResponseT, response);
                if (qiniuTokenDtoResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                    String qiNiuToken = qiniuTokenDtoResponseT.getBizData().getToken();
                    AppPref.getInstance().saveQiNiuToken(qiNiuToken);
                    if (sUploadManager == null) {
                        sUploadManager = new UploadManager();
                    }

                    //加密
                    String key = AppPref.getInstance().getUserId() + "_" + String.valueOf(System.currentTimeMillis());
                    Md5FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
                    key = md5FileNameGenerator.generate(key) + ".jpg";
                    sUploadManager.put(filePath, key, qiNiuToken, upCompletionHandler,
                            new UploadOptions(null, null, false, null, upCancellationSignal));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
            }
        });
    }

    public static void upLoadVideo(Context context, String postType, String mediaType,  final String filePath,
                                   final UpCompletionHandler upCompletionHandler, final UpCancellationSignal upCancellationSignal, final UpProgressHandler upProgressHandler) {
        IQiNiuStubService stubService = RetrofitUtil.createApi(IQiNiuStubService.class);
        stubService.qiniuAccessToken(AppPref.getInstance().getAccessToken(), postType, mediaType, new RetrofitUtil.ActivityCallback<ResponseT<QiniuTokenDto>>(context) {
            @Override
            public void success(ResponseT<QiniuTokenDto> qiniuTokenDtoResponseT, Response response) {
                super.success(qiniuTokenDtoResponseT, response);
                if (qiniuTokenDtoResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                    String qiNiuToken = qiniuTokenDtoResponseT.getBizData().getToken();
                    AppPref.getInstance().saveQiNiuToken(qiNiuToken);
                    if (sUploadManager == null) {
                        sUploadManager = new UploadManager();
                    }
                    String key = AppPref.getInstance().getUserId()+"_"+ String.valueOf(System.currentTimeMillis());
                    Md5FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
                    key = md5FileNameGenerator.generate(key) +".mp4";

                    sUploadManager.put(filePath, key, qiNiuToken, upCompletionHandler,
                            new UploadOptions(null, "video/mp4", false, upProgressHandler, upCancellationSignal));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
            }
        });
    }
    public static void upLoadImages(Context context, String postType, String mediaType, final String filePath, final String key,
                                    final UpCompletionHandler upCompletionHandler, final CancelCallBack cancelCallBack, final UpProgressHandler upProgressHandler){
        IQiNiuStubService stubService = RetrofitUtil.createApi(IQiNiuStubService.class);
        stubService.qiniuAccessToken(AppPref.getInstance().getAccessToken(),postType,mediaType,
                new RetrofitUtil.ActivityCallback<ResponseT<QiniuTokenDto>>(context){
                    @Override
                    public void success(ResponseT<QiniuTokenDto> qiniuTokenDtoResponseT, Response response) {
                        super.success(qiniuTokenDtoResponseT, response);
                        if (qiniuTokenDtoResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            String qiNiuToken = qiniuTokenDtoResponseT.getBizData().getToken();
                            AppPref.getInstance().saveQiNiuToken(qiNiuToken);
                            if (sUploadManager == null) {
                                sUploadManager = new UploadManager();
                            }
                            sUploadManager.put(filePath,key,qiNiuToken,upCompletionHandler,
                                    new UploadOptions(null, null, false, upProgressHandler, new UpCancellationSignal() {
                                        @Override
                                        public boolean isCancelled() {
                                            return cancelCallBack.isCancelled(key);
                                        }
                                    }));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                    }
                });
    }

    public static void upLoadAudio(Context context, String postType, String mediaType, final String filePath,
                                   final UpCompletionHandler handler, final UpCancellationSignal cancellationSignal, final UpProgressHandler upProgressHandler){
        IQiNiuStubService stubService = RetrofitUtil.createApi(IQiNiuStubService.class);
        stubService.qiniuAccessToken(AppPref.getInstance().getAccessToken(),postType,mediaType,
                new RetrofitUtil.ActivityCallback<ResponseT<QiniuTokenDto>>(context){
                    @Override
                    public void success(ResponseT<QiniuTokenDto> qiniuTokenDtoResponseT, Response response) {
                        super.success(qiniuTokenDtoResponseT, response);
                        if (qiniuTokenDtoResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            String qiNiuToken = qiniuTokenDtoResponseT.getBizData().getToken();
                            AppPref.getInstance().saveQiNiuToken(qiNiuToken);
                            if (sUploadManager == null) {
                                sUploadManager = new UploadManager();
                            }
                            String key = AppPref.getInstance().getUserId()+"_"+ String.valueOf(System.currentTimeMillis());
                            Md5FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
                            key = md5FileNameGenerator.generate(key) + ".mp3";
                            sUploadManager.put(filePath,key,qiNiuToken,handler,
                                    new UploadOptions(null,null,false,upProgressHandler,cancellationSignal));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                    }
                });
    }

    public interface CancelCallBack {
        boolean isCancelled(String key);
    }


    public static String getImageKey(){
        String key = AppPref.getInstance().getUserId()+"_" + String.valueOf(System.currentTimeMillis());
        Md5FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
        key = md5FileNameGenerator.generate(key) + ".jpg";
        return key;
    }
}
