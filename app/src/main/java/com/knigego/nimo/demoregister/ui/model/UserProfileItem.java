package com.knigego.nimo.demoregister.ui.model;

import com.crooods.wd.dto.UserProfileDto;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

/**
 * Created by ThinkPad on 2017/4/2.
 */

public class UserProfileItem extends ItemViewTypeHelperManager.ItemViewData {
    public UserProfileDto getUserProfileDto() {
        return mUserProfileDto;
    }

    public void setUserProfileDto(UserProfileDto userProfileDto) {
        mUserProfileDto = userProfileDto;
    }

    private UserProfileDto mUserProfileDto;

    public UserProfileItem(UserProfileDto userProfileDto , ItemViewTypeHelperManager.ItemViewTypeHelper itemViewTypeHelper) {
        this.mItemViewTypeHelper = itemViewTypeHelper;
        mUserProfileDto = userProfileDto;
    }
}
