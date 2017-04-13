package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.activities.ReleaseChooseActivity;
import com.knigego.nimo.demoregister.ui.activities.ReleaseMomentActivity;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * Created by ThinkPad on 2017/4/2.
 */

public class UserContentEmptyTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper {

    private boolean isMy;
    private int mType;

    public UserContentEmptyTypeHelper(Context context, int layoutId, boolean isMy, int type) {
        super(context, layoutId);
        this.isMy = isMy;
        this.mType = type;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new EmptyHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        EmptyHolder emptyHolder = (EmptyHolder) viewHolder;
        if (isMy) {
            emptyHolder.mTextEmpty.setText(R.string.user_content_empty);
            emptyHolder.mImageRelease.setVisibility(View.VISIBLE);
        } else {
            emptyHolder.mTextEmpty.setText(R.string.other_user_content_empty);
            emptyHolder.mImageRelease.setVisibility(View.INVISIBLE);
        }

        emptyHolder.mImageRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType == AppConstants.TYPE_STAGE) {
                    mContext.startActivity(new Intent(mContext, ReleaseChooseActivity.class));
                } else if (mType == AppConstants.TYPE_MOMENT) {
                    mContext.startActivity(new Intent(mContext,ReleaseMomentActivity.class));
                }
            }
        });
    }

    public class EmptyHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.image_release)
        ImageView mImageRelease;
        @Bind(R.id.text_empty)
        TextView mTextEmpty;

        public EmptyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
