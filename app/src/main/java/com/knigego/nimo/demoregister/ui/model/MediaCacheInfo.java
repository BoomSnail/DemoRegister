package com.knigego.nimo.demoregister.ui.model;

import android.text.TextUtils;

import com.crooods.wd.dto.post.StageDto;
import com.crooods.wd.enums.MediaTypeEnum;
import com.knigego.nimo.demoregister.util.FileHelper;
import com.knigego.nimo.demoregister.AppConfig;

import java.io.File;

/**
 * 缓存视频的处理
 * <p>
 * Created by ThinkPad on 2017/4/2.
 */

public class MediaCacheInfo {
    private int notificationId;//通知栏ID
    private StageDto mStageDto;
    private String fileName;
    private boolean isDownload;//是否在下载完成
    private String mediaType;
    private long size;
    private boolean hadCheck;//是否选中
    private int type;//mediaType为视频，type为舞台或者动态类型

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public StageDto getStageDto() {
        return mStageDto;
    }

    public void setStageDto(StageDto stageDto) {
        mStageDto = stageDto;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isHadCheck() {
        return hadCheck;
    }

    public void setHadCheck(boolean hadCheck) {
        this.hadCheck = hadCheck;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isFileExist() {
        String filePath = null;
        if (mediaType.equals(MediaTypeEnum.AUDIO.getCode())) {
            filePath = AppConfig.getDemoRegisterFile() + AppConfig.PATH_AUDIO_DOWNLOAD;
        } else if (mediaType.equals(MediaTypeEnum.VIDEO.getCode())) {
            filePath = AppConfig.getDemoRegisterFile() + AppConfig.PATH_VIDEO_DOWNLOAD;
        }

        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File mediaDir = new File(filePath);
        if (!mediaDir.exists()) {
            if (!mediaDir.getParentFile().exists()) {
                mediaDir.getParentFile().mkdirs();
            }

            mediaDir.mkdirs();
            return false;

        }
        File mediaFile = new File(mediaDir, fileName);
        if (mediaFile.exists() && mediaFile.isFile()) {
            return true;
        } else {
            return false;
        }
    }

    public void removeFile(){
        String filePath = null;
        if (mediaType.equals(MediaTypeEnum.AUDIO.getCode())) {
            filePath = AppConfig.getDemoRegisterFile() + AppConfig.PATH_AUDIO_DOWNLOAD;
        } else if (mediaType.equals(MediaTypeEnum.VIDEO.getCode())){
            filePath = AppConfig.getDemoRegisterFile() + AppConfig.PATH_VIDEO_DOWNLOAD;
        }

        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        File mediaDir = new File(filePath);
        File mediaFile = new File(mediaDir,fileName);
        FileHelper.delete(mediaFile);
    }
}
