package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.model.DancerInfo;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.util.GlideCircleTransform;
import com.knigego.nimo.demoregister.util.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ThinkPad on 2017/4/14.
 */

public class SearchItemTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper {



    public SearchItemTypeHelper(Context context, int layout) {
        super(context, layout);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {

        return new SearchHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        SearchHolder searchHolder = (SearchHolder) viewHolder;
        final DancerInfo dancerInfo = (DancerInfo) data;
        //头像
        Glide.with(mContext).load(dancerInfo.getAppUser().getAvator())
                .placeholder(R.drawable.rc_ic_def_msg_portrait)
                .transform(new GlideCircleTransform(mContext))
                .into(searchHolder.mImageAvatar);
        //名字
        searchHolder.mTextName.setText(dancerInfo.getAppUser().getNickName());

        if (mBaseAdapter.getItemCount() == 1) {
            searchHolder.mViewTopDivider.setVisibility(View.VISIBLE);
            searchHolder.mViewBottomDivider.setVisibility(View.VISIBLE);
            searchHolder.mViewBottomShortDivider.setVisibility(View.GONE);
        } else {
            if (position == 0) {
                searchHolder.mViewTopDivider.setVisibility(View.VISIBLE);
                searchHolder.mViewBottomDivider.setVisibility(View.GONE);
                searchHolder.mViewBottomShortDivider.setVisibility(View.VISIBLE);
            } else if (position == (mBaseAdapter.getItemCount() - 1)) {
                searchHolder.mViewTopDivider.setVisibility(View.GONE);
                searchHolder.mViewBottomDivider.setVisibility(View.VISIBLE);
                searchHolder.mViewBottomShortDivider.setVisibility(View.GONE);
            } else {
                searchHolder.mViewTopDivider.setVisibility(View.GONE);
                searchHolder.mViewBottomDivider.setVisibility(View.GONE);
                searchHolder.mViewBottomShortDivider.setVisibility(View.VISIBLE);
            }
        }

        searchHolder.mLayoutSearchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.goToPersonalInfo(mContext,dancerInfo.getAppUser().getNickName(),
                        dancerInfo.getAppUser().getId(),dancerInfo.getAppUser().getRole());
            }
        });
    }

    public class SearchHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.view_top_divider)
        View mViewTopDivider;
        @Bind(R.id.image_avatar)
        ImageView mImageAvatar;
        @Bind(R.id.text_name)
        TextView mTextName;
        @Bind(R.id.layout_search_item)
        LinearLayout mLayoutSearchItem;
        @Bind(R.id.view_bottom_short_divider)
        View mViewBottomShortDivider;
        @Bind(R.id.view_bottom_divider)
        View mViewBottomDivider;
        public SearchHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
