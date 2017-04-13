package com.knigego.nimo.demoregister.ui.model;

import com.crooods.wd.dto.post.StageDto;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

/**
 * Created by ThinkPad on 2017/4/5.
 */

public class ShareInfo  extends ItemViewTypeHelperManager.ItemViewData{
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public StageDto getStageDto() {
        return mStageDto;
    }

    public void setStageDto(StageDto stageDto) {
        mStageDto = stageDto;
    }

    private StageDto mStageDto;
}
