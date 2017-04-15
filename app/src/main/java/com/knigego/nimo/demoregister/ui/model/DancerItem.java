package com.knigego.nimo.demoregister.ui.model;

import com.crooods.wd.dto.DancerDto;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

/**
 * Created by ThinkPad on 2017/4/13.
 */

public class DancerItem extends ItemViewTypeHelperManager.ItemViewData{
    private DancerDto mDancerDto;

    public DancerItem() {

    }

    public DancerDto getDancerDto() {
        return mDancerDto;
    }

    public void setDancerDto(DancerDto dancerDto) {
        mDancerDto = dancerDto;
    }

    public DancerItem(DancerDto dancerDto, ItemViewTypeHelperManager.ItemViewTypeHelper itemViewTypeHelper) {
        mDancerDto = dancerDto;
        mItemViewTypeHelper = itemViewTypeHelper;
    }
}
