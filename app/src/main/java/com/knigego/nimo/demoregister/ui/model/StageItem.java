package com.knigego.nimo.demoregister.ui.model;

import com.crooods.wd.dto.post.StageDto;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

/**
 * Created by ThinkPad on 2017/3/30.
 */

public class StageItem extends ItemViewTypeHelperManager.ItemViewData {
    public StageItem() {

    }

    public StageDto getStageDto() {
        return mStageDto;
    }

    public void setStageDto(StageDto stageDto) {
        mStageDto = stageDto;
    }

    private StageDto mStageDto;

    public StageItem(StageDto stageDto,ItemViewTypeHelperManager.ItemViewTypeHelper itemViewTypeHelper) {
        mStageDto = stageDto;
        mItemViewTypeHelper = itemViewTypeHelper;
    }


}
