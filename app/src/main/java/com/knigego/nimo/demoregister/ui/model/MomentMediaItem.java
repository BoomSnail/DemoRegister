package com.knigego.nimo.demoregister.ui.model;

/**
 * Created by ThinkPad on 2017/4/8.
 */

public class MomentMediaItem {
    public static final  int MEDIA_TYPE_IMAGE = 0;
    public static final int MEDIA_TYPE_VIDEO = 1;

    private int mediaType;
    private String filePath;
    private String key;
    private String imageUrl;//图片网络地址
    private String coverPath;
    private boolean isAddView;
    private boolean isUpLoading;

    public MomentMediaItem(boolean isAddView) {
        this.isAddView = isAddView;
    }

    public MomentMediaItem() {

    }

    public static int getMediaTypeImage() {
        return MEDIA_TYPE_IMAGE;
    }

    public static int getMediaTypeVideo() {
        return MEDIA_TYPE_VIDEO;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public boolean isAddView() {
        return isAddView;
    }

    public void setAddView(boolean addView) {
        isAddView = addView;
    }

    public boolean isUpLoading() {
        return isUpLoading;
    }

    public void setUpLoading(boolean upLoading) {
        isUpLoading = upLoading;
    }
}
