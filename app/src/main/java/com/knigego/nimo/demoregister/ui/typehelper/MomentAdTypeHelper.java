package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.model.AdInfo;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.util.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ThinkPad on 2017/4/4.
 */

public class MomentAdTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper{

    public MomentAdTypeHelper(Context context, int layout) {
        super(context, layout);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new AdViewHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        AdViewHolder adViewHolder = (AdViewHolder) viewHolder;
        final AdInfo adInfo = (AdInfo) data;
        Glide.with(mContext).load(adInfo.getAd().getImgUrl())
                .placeholder(R.color.image_default_color)
                .error(R.drawable.error_image_moment_ad)
                .centerCrop().into(adViewHolder.mImageView);

        adViewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.goToWebView(mContext,adInfo.getAd().getLinkUrl(),adInfo.getAd().getName());
            }
        });
    }

    public class AdViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.image_ad)
        ImageView mImageView;
        public AdViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
