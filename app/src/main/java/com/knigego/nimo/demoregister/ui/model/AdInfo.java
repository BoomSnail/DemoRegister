package com.knigego.nimo.demoregister.ui.model;

import com.crooods.wd.domain.Ad;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

/**
 * Created by ThinkPad on 2017/4/4.
 */

public class AdInfo  extends ItemViewTypeHelperManager.ItemViewData{
    private Ad mAd;

    public Ad getAd() {
        return mAd;
    }

    public void setAd(Ad ad) {
        mAd = ad;
    }
}
