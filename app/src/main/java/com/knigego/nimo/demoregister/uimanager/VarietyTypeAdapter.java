package com.knigego.nimo.demoregister.uimanager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ThinkPad on 2017/2/28.
 */

public class VarietyTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ItemViewTypeHelperManager.ItemViewData> mItemViewDatas;
    private ItemViewTypeHelperManager mItemViewTypeHelperManager;

    public VarietyTypeAdapter(ItemViewTypeHelperManager itemViewTypeHelperManager,
                              List<ItemViewTypeHelperManager.ItemViewData> itemViewDatas) {
        mItemViewTypeHelperManager = itemViewTypeHelperManager;
        mItemViewDatas = itemViewDatas;
    }

    public void addListData(List<ItemViewTypeHelperManager.ItemViewData> itemViewData){
        mItemViewDatas.addAll(itemViewData);
        notifyDataSetChanged();
    }

    public void setListData(List<ItemViewTypeHelperManager.ItemViewData> itemViewDatas){
        mItemViewDatas = itemViewDatas;
        notifyDataSetChanged();
    }

    public List<ItemViewTypeHelperManager.ItemViewData> getItemViewDatas() {
        return mItemViewDatas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //每个itemviewType的createViewHolder
        return mItemViewTypeHelperManager.getItemTypeHelperAt(viewType)
                .createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ItemViewTypeHelperManager.ItemViewData itemViewData = mItemViewDatas.get(position);
        itemViewData.getItemViewTypeHelper().setAdapter(this);
        itemViewData.getItemViewTypeHelper().updateData(holder,itemViewData,position);
    }

    @Override
    public int getItemViewType(int position) {
        ItemViewTypeHelperManager.ItemViewData itemViewData = mItemViewDatas.get(position);
        try {

            int type = mItemViewTypeHelperManager.getTypeView(itemViewData.getItemViewTypeHelper());

            return type;
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return mItemViewDatas.size();
    }

    public List<ItemViewTypeHelperManager.ItemViewData> getListData() {
        return mItemViewDatas;


    }
}
