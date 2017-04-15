package com.knigego.nimo.demoregister.ui.model;

import com.crooods.wd.domain.AppUser;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

/**
 * Created by ThinkPad on 2017/4/14.
 */

public class DancerInfo extends ItemViewTypeHelperManager.ItemViewData{
    public AppUser getAppUser() {
        return mAppUser;
    }

    public void setAppUser(AppUser appUser) {
        mAppUser = appUser;
    }

    private AppUser mAppUser;

}
