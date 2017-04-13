package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

/**
 * Created by ThinkPad on 2017/3/29.
 */

public class SuperStarTitleHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper{


    public SuperStarTitleHelper(Context context, int layout) {
        super(context, layout);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new EmptyViewHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {

    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder{

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

}
