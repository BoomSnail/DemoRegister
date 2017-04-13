package com.knigego.nimo.demoregister.ui.model;

import com.crooods.wd.dto.StarShowRangeDto;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

/**
 * Created by ThinkPad on 2017/3/29.
 */

public class SuperStarItem extends ItemViewTypeHelperManager.ItemViewData {

    public SuperStarItem() {

    }

    public StarShowRangeDto getStarShowRangeDto() {
        return mStarShowRangeDto;
    }

    public void setStarShowRangeDto(StarShowRangeDto starShowRangeDto) {
        mStarShowRangeDto = starShowRangeDto;
    }

    private StarShowRangeDto mStarShowRangeDto;

    public SuperStarItem(StarShowRangeDto starShowRangeDto , ItemViewTypeHelperManager.ItemViewTypeHelper itemViewTypeHelper) {
        mItemViewTypeHelper = itemViewTypeHelper;
        mStarShowRangeDto = starShowRangeDto;
    }


}
