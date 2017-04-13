package com.knigego.nimo.demoregister.util;

import com.crooods.wd.enums.MediaTypeEnum;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.model.MediaCacheInfo;

import java.util.List;

/**
 * 音频管理
 * Created by ThinkPad on 2017/4/2.
 */

public class MediaCacheDataHelper {

    public static void saveMediaCache(MediaCacheInfo mediaCacheInfo){
        List<MediaCacheInfo> mediaCacheInfos = AppPref.getInstance().getMyMediaCache();
        mediaCacheInfos.add(mediaCacheInfo);
        AppPref.getInstance().saveMyMediaCache(mediaCacheInfos);
    }

    public static void removeMediaCache(MediaCacheInfo mediaCacheInfo){
        List<MediaCacheInfo> mediaCacheInfos = AppPref.getInstance().getMyMediaCache();
        for (int i = 0; i < mediaCacheInfos.size(); i++) {
            if (mediaCacheInfo.getFileName().equals(mediaCacheInfos.get(i).getFileName())) {
                mediaCacheInfos.remove(i);
                AppPref.getInstance().saveMyMediaCache(mediaCacheInfos);
                removeMediaCache(mediaCacheInfo);//如果有多个则都删除
                return;
            }

        }
    }

    public static int getMyVideoCacheNum() {
        int num = 0;
        List<MediaCacheInfo> mediaCacheInfos = AppPref.getInstance().getMyMediaCache();
        for (int i = 0; i < mediaCacheInfos.size(); i++) {
            MediaCacheInfo mediaCacheInfo = mediaCacheInfos.get(i);
            if (mediaCacheInfo.isDownload() && mediaCacheInfo.isFileExist() &&
                    mediaCacheInfo.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
                num++;
            }
        }
        return num;
    }

    public static int getMyAudioCacheNum() {
        int num = 0;
        List<MediaCacheInfo> mediaCacheInfos = AppPref.getInstance().getMyMediaCache();
        for (int i = 0; i < mediaCacheInfos.size(); i++) {
            MediaCacheInfo mediaCacheInfo = mediaCacheInfos.get(i);
            if (mediaCacheInfo.isFileExist() && mediaCacheInfo.isDownload() && mediaCacheInfo.getMediaType()
                    .equals(MediaTypeEnum.AUDIO.getCode())) {
                num++;
            }
        }
        return num;
    }
}
