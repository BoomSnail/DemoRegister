package com.knigego.nimo.demoregister.uimanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad on 2017/2/28.
 */

public class ItemViewTypeHelperManager {

    private List<ItemViewTypeHelper> mItemViewTypeHelpers;

    public ItemViewTypeHelperManager() {
        mItemViewTypeHelpers = new ArrayList<>();
    }

    public void addType(ItemViewTypeHelper type){
        mItemViewTypeHelpers.add(type);
    }

    public ItemViewTypeHelper getItemViewTypeHelper(int layoutId){
        for (int i = 0; i < mItemViewTypeHelpers.size(); i++) {
            ItemViewTypeHelper itemViewTypeHelper = mItemViewTypeHelpers.get(i);
            if (itemViewTypeHelper.getLayout() == layoutId) {
                return itemViewTypeHelper;
            }
        }
        return null;
    }

    public boolean hasType(ItemViewTypeHelper itemViewTypeHelper){
        return mItemViewTypeHelpers.contains(itemViewTypeHelper);
    }

    public void renoveType(ItemViewTypeHelper itemViewTypeHelper){
        mItemViewTypeHelpers.remove(itemViewTypeHelper);
    }

    public int getTypeCount(){
        return mItemViewTypeHelpers.size();
    }

    public int getTypeView(ItemViewTypeHelper itemViewTypeHelper){
        int index = mItemViewTypeHelpers.indexOf(itemViewTypeHelper);
        return index;
    }
    public ItemViewTypeHelper getItemTypeHelperAt(int index){
        return mItemViewTypeHelpers.get(index);
    }

    public static abstract class ItemViewTypeHelper {

        protected LayoutInflater mLayoutInflater;
        protected int mLayout = -1;
        protected Context mContext;
        protected View mView;

        public ItemViewTypeHelper(Context context, int layout) {
            mContext = context;
            mLayout = layout;
            mLayoutInflater = LayoutInflater.from(context);
        }

        public Context getContext() {
            return mContext;
        }

        public int getLayout() {
            return mLayout;
        }

        public RecyclerView.ViewHolder createViewHolder(ViewGroup viewGroup) {
            View view = mLayoutInflater.inflate(mLayout, viewGroup, false);
            return createViewHolder(view);
        }

        public abstract RecyclerView.ViewHolder createViewHolder(View view);

        public abstract void updateData(RecyclerView.ViewHolder viewHolder,ItemViewData data,int position);

        public VarietyTypeAdapter mBaseAdapter;
        public void setAdapter(VarietyTypeAdapter varietyTypeAdapter) {
            mBaseAdapter = varietyTypeAdapter;
        }
    }

    public static class ItemViewData{
        public ItemViewTypeHelper mItemViewTypeHelper;

        public ItemViewTypeHelper getItemViewTypeHelper() {
            return mItemViewTypeHelper;
        }

        public void setItemViewTypeHelper(ItemViewTypeHelper itemViewTypeHelper) {
            mItemViewTypeHelper = itemViewTypeHelper;
        }
    }
}
