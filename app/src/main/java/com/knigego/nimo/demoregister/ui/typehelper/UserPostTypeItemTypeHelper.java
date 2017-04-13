package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.fragment.UserInfoFragment;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ThinkPad on 2017/4/2.
 */

public class UserPostTypeItemTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper{
    private Fragment mFragment;
    private boolean isMy;
    public UserPostTypeItemTypeHelper(Context context, int layout, Fragment fragment, boolean isMy) {
        super(context, layout);
        this.mFragment = fragment;
        this.isMy = isMy;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ChooseViewHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        final ChooseViewHolder chooseViewHolder = (ChooseViewHolder) viewHolder;
        if (isMy) {
            chooseViewHolder.mBtnTypeStage.setText(R.string.my_stage);
            chooseViewHolder.mBtnTypeMoment.setText(R.string.my_moment);
        } else {
            chooseViewHolder.mBtnTypeStage.setText(R.string.ta_stage);
            chooseViewHolder.mBtnTypeMoment.setText(R.string.ta_moment);
        }

        chooseViewHolder.mBtnTypeStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chooseViewHolder.mBtnTypeStage.isSelected()) {
                    chooseViewHolder.mBtnTypeStage.setSelected(true);
                    chooseViewHolder.mBtnTypeMoment.setSelected(false);
                    if (mFragment instanceof UserInfoFragment) {
                        ((UserInfoFragment)mFragment).setTypeChoose(AppConstants.TYPE_STAGE);
                    }
                }
            }
        });
        chooseViewHolder.mBtnTypeMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!chooseViewHolder.mBtnTypeMoment.isSelected()){
                    chooseViewHolder.mBtnTypeStage.setSelected(false);
                    chooseViewHolder.mBtnTypeMoment.setSelected(true);
                    if(mFragment instanceof UserInfoFragment){
                        ((UserInfoFragment)mFragment).setTypeChoose(AppConstants.TYPE_MOMENT);
                    }
                }
            }
        });
    }

    public class ChooseViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.btn_type_stage)
        Button mBtnTypeStage;
        @Bind(R.id.btn_type_moment)
        Button mBtnTypeMoment;
        public ChooseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
