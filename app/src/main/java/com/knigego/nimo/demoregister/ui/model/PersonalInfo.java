package com.knigego.nimo.demoregister.ui.model;

import com.crooods.wd.dto.UserDto;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

/**
 * Created by ThinkPad on 2017/4/2.
 */

public class PersonalInfo extends ItemViewTypeHelperManager.ItemViewData{
    private UserDto mUserDto;

    public UserDto getUserDto() {
        return mUserDto;
    }

    public void setUserDto(UserDto userDto) {
        mUserDto = userDto;
    }
}
