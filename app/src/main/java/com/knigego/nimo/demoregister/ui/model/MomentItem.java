package com.knigego.nimo.demoregister.ui.model;

import com.crooods.wd.dto.post.MomentDto;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

/**
 * Created by ThinkPad on 2017/4/2.
 */

public class MomentItem extends ItemViewTypeHelperManager.ItemViewData{
    private MomentDto mMomentDto;

    public MomentDto getMomentDto() {
        return mMomentDto;
    }

    public void setMomentDto(MomentDto momentDto) {
        mMomentDto = momentDto;
    }
}
