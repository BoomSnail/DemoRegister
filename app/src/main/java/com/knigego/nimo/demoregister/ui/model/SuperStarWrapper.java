package com.knigego.nimo.demoregister.ui.model;

import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

import java.util.List;

/**
 * Created by ThinkPad on 2017/3/30.
 */

public class SuperStarWrapper extends ItemViewTypeHelperManager.ItemViewData {
    private List<SuperStarItem> mSuperStarItems;

    public SuperStarWrapper(){}
    public List<SuperStarItem> getSuperStarItems() {
        return mSuperStarItems;
    }

    public void setSuperStarItems(List<SuperStarItem> superStarItems) {
        mSuperStarItems = superStarItems;
    }

    public SuperStarWrapper(List<SuperStarItem> superStarItems, ItemViewTypeHelperManager.ItemViewTypeHelper itemViewTypeHelper) {
        mItemViewTypeHelper = itemViewTypeHelper;
        mSuperStarItems = superStarItems;
    }
}
