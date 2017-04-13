package com.knigego.nimo.demoregister.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.dto.StringWrapper;
import com.crooods.wd.domain.AppUser;
import com.crooods.wd.dto.post.StageDto;
import com.crooods.wd.dto.post.StageImageDto;
import com.crooods.wd.dto.post.StageVideoDto;
import com.crooods.wd.enums.MediaTypeEnum;
import com.crooods.wd.service.IFriendshipsStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.activities.PictureViewActivity;
import com.knigego.nimo.demoregister.ui.model.SuperStarItem;
import com.knigego.nimo.demoregister.util.BrodcastHelper;
import com.knigego.nimo.demoregister.util.CommonUtils;
import com.knigego.nimo.demoregister.util.GlideCircleTransform;
import com.knigego.nimo.demoregister.util.UIHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *
 * Created by ThinkPad on 2017/3/29.
 */

public class SuperStarAdapter extends RecyclerView.Adapter {
    private List<SuperStarItem> mSuperStarItems;
    private Context mContext;

    public SuperStarAdapter(Context context, List<SuperStarItem> superStarItems) {
        mSuperStarItems = superStarItems;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_super_star_item,parent,false);

        return new SuperStarHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SuperStarHolder starHolder = (SuperStarHolder) holder;
        final SuperStarItem superStarItem = mSuperStarItems.get(position);
        final AppUser appUser = superStarItem.getStarShowRangeDto().getUser();

        showStages(starHolder, superStarItem.getStarShowRangeDto().getStages());
        //avatar
        Glide.with(mContext).load(appUser.getAvator()).placeholder(R.drawable.rc_ic_def_msg_portrait)
                .transform(new GlideCircleTransform(mContext)).into(starHolder.mImageAvatar);
        //nickName
        starHolder.mTextName.setText(appUser.getNickName());
        //address
        starHolder.mTextArea.setText(appUser.getAddress());
        //introSelf
        starHolder.mTextIntro.setText(appUser.getIntro());

        //follow or unFollow
        if (superStarItem.getStarShowRangeDto().isFollowed()) {
            starHolder.mBtnAttention.setBackgroundResource(R.drawable.btn_star_has_attention);
        } else {
            starHolder.mBtnAttention.setBackgroundResource(R.drawable.btn_star_attention_selector);
        }

        starHolder.mBtnAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (superStarItem.getStarShowRangeDto().isFollowed()) {
                    cancelFollow(superStarItem);
                } else {
                    follow(superStarItem);
                }
            }
        });

        //如果超级明星有自己，则关注按钮不可见
        if (superStarItem.getStarShowRangeDto().getUser().getId().longValue() ==
                AppPref.getInstance().getUserId()) {
            starHolder.mBtnAttention.setVisibility(View.GONE);
        } else {
            starHolder.mBtnAttention.setVisibility(View.VISIBLE);
        }

        starHolder.mLayoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.goToPersonalInfo(mContext, appUser.getNickName(), appUser.getId(), appUser.getRole());
            }
        });

    }

    private void showStages(SuperStarHolder superStarHolder, List<StageDto> stages) {
        //没有则不显示
        if (stages.size() == 0) {
            setNotContent(superStarHolder.mLayout1, superStarHolder.mImage1, superStarHolder.mImagePlay1);
            setNotContent(superStarHolder.mLayout2, superStarHolder.mImage2, superStarHolder.mImagePlay2);
            setNotContent(superStarHolder.mLayout3, superStarHolder.mImage3, superStarHolder.mImagePlay3);
            setNotContent(superStarHolder.mLayout4, superStarHolder.mImage4, superStarHolder.mImagePlay4);
        } else if (stages.size() == 1) {//一个则显示第一个iamgeview
            superStarHolder.mLayout1.setVisibility(View.VISIBLE);
            showStage(superStarHolder.mLayout1,superStarHolder.mImage1,superStarHolder.mImagePlay1,stages.get(0));
            setNotContent(superStarHolder.mLayout2, superStarHolder.mImage2, superStarHolder.mImagePlay2);
            setNotContent(superStarHolder.mLayout3, superStarHolder.mImage3, superStarHolder.mImagePlay3);
            setNotContent(superStarHolder.mLayout4, superStarHolder.mImage4, superStarHolder.mImagePlay4);
        } else if (stages.size() == 2) {
            superStarHolder.mLayout1.setVisibility(View.VISIBLE);
            showStage(superStarHolder.mLayout1,superStarHolder.mImage1,superStarHolder.mImagePlay1,stages.get(0));
            superStarHolder.mLayout2.setVisibility(View.VISIBLE);
            showStage(superStarHolder.mLayout2,superStarHolder.mImage2,superStarHolder.mImagePlay2,stages.get(1));
            setNotContent(superStarHolder.mLayout3,superStarHolder.mImage3,superStarHolder.mImagePlay3);
            setNotContent(superStarHolder.mLayout4,superStarHolder.mImage4,superStarHolder.mImagePlay4);
        } else if (stages.size() == 3) {
            superStarHolder.mLayout1.setVisibility(View.VISIBLE);
            showStage(superStarHolder.mLayout1,superStarHolder.mImage1,superStarHolder.mImagePlay1,stages.get(0));
            superStarHolder.mLayout2.setVisibility(View.VISIBLE);
            showStage(superStarHolder.mLayout2,superStarHolder.mImage2,superStarHolder.mImagePlay2,stages.get(1));
            superStarHolder.mLayout3.setVisibility(View.VISIBLE);
            showStage(superStarHolder.mLayout3,superStarHolder.mImage3,superStarHolder.mImagePlay3,stages.get(2));
            setNotContent(superStarHolder.mLayout4,superStarHolder.mImage4,superStarHolder.mImagePlay4);

        } else {
            superStarHolder.mLayout1.setVisibility(View.VISIBLE);
            showStage(superStarHolder.mLayout1,superStarHolder.mImage1,superStarHolder.mImagePlay1,stages.get(0));
            superStarHolder.mLayout2.setVisibility(View.VISIBLE);
            showStage(superStarHolder.mLayout2,superStarHolder.mImage2,superStarHolder.mImagePlay2,stages.get(1));
            superStarHolder.mLayout3.setVisibility(View.VISIBLE);
            showStage(superStarHolder.mLayout3,superStarHolder.mImage3,superStarHolder.mImagePlay3,stages.get(2));
            superStarHolder.mLayout4.setVisibility(View.VISIBLE);
            showStage(superStarHolder.mLayout4,superStarHolder.mImage4,superStarHolder.mImagePlay4,stages.get(3));
        }


    }

    private void showStage(RelativeLayout layout, ImageView image, ImageView imagePlay, StageDto stageDto) {
        layout.setEnabled(true);//设置可点击
        if (stageDto.getMediaType().equals(MediaTypeEnum.IMAGE.getCode())) {
            StageImageDto stageImageDto = stageDto.getImageData();
            Glide.with(mContext).load(stageImageDto.getImages().get(0).getUrl()).placeholder(R.color.image_default_color)
                    .error(R.drawable.error_image_a_half_w).centerCrop().into(image);
            imagePlay.setVisibility(View.GONE);
            layout.setOnClickListener(new GotoPictureViewListener(CommonUtils.getPictures(stageImageDto.getImages()),0,mContext));
        } else  {
            imagePlay.setVisibility(View.VISIBLE);
            final StageVideoDto stageVideoDto = stageDto.getVideoData();
            Glide.with(mContext).load(stageVideoDto.getCoverUrl()).placeholder(R.color.image_default_color)
                    .error(R.drawable.error_image_a_half_w).into(image);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.goToVideoPlayActivity(mContext,stageVideoDto.getfUrl());
                }
            });

        }

    }

    public static class GotoPictureViewListener implements View.OnClickListener{

        private String[] pictures;
        private int position;
        private Context context;

        public GotoPictureViewListener(String[] pictures, int position, Context context) {
            this.pictures = pictures;
            this.position = position;
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, PictureViewActivity.class);
            intent.putExtra(PictureViewActivity.PICTURES,pictures);
            intent.putExtra(PictureViewActivity.POSITION,position);
            context.startActivity(intent);
        }
    }

    private void setNotContent(RelativeLayout layout, ImageView image, ImageView imagePlay) {
        image.setImageResource(R.drawable.super_star_not_content_bg);
        imagePlay.setVisibility(View.GONE);
        layout.setEnabled(false);//点击没效果
    }

    private void follow(final SuperStarItem superStarItem) {
        IFriendshipsStubService stubService = RetrofitUtil.createApi(IFriendshipsStubService.class);
        stubService.friendsCreate(AppPref.getInstance().getAccessToken(), superStarItem.getStarShowRangeDto().getUser().getId(),
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            superStarItem.getStarShowRangeDto().setFollowed(true);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                    }
                });
    }

    private void cancelFollow(final SuperStarItem superStarItem) {
        IFriendshipsStubService stubService = RetrofitUtil.createApi(IFriendshipsStubService.class);
        stubService.friendsCancel(AppPref.getInstance().getAccessToken(), superStarItem.getStarShowRangeDto().getUser().getId(),
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            superStarItem.getStarShowRangeDto().setFollowed(false);
                            notifyDataSetChanged();

                            //取消关注，发送广播更新个人主页关注数目
                            BrodcastHelper.sendUserInfoUpdateAction(mContext);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mSuperStarItems.size();
    }


    static class SuperStarHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image_1)
        ImageView mImage1;
        @Bind(R.id.image_play_1)
        ImageView mImagePlay1;
        @Bind(R.id.layout_1)
        RelativeLayout mLayout1;
        @Bind(R.id.image_2)
        ImageView mImage2;
        @Bind(R.id.image_play_2)
        ImageView mImagePlay2;
        @Bind(R.id.layout_2)
        RelativeLayout mLayout2;
        @Bind(R.id.image_3)
        ImageView mImage3;
        @Bind(R.id.image_play_3)
        ImageView mImagePlay3;
        @Bind(R.id.layout_3)
        RelativeLayout mLayout3;
        @Bind(R.id.image_4)
        ImageView mImage4;
        @Bind(R.id.image_play_4)
        ImageView mImagePlay4;
        @Bind(R.id.layout_4)
        RelativeLayout mLayout4;
        @Bind(R.id.image_avatar)
        ImageView mImageAvatar;
        @Bind(R.id.text_name)
        TextView mTextName;
        @Bind(R.id.text_area)
        TextView mTextArea;
        @Bind(R.id.text_intro)
        TextView mTextIntro;
        @Bind(R.id.btn_attention)
        Button mBtnAttention;
        @Bind(R.id.layout_user)
        LinearLayout mLayoutUser;

        SuperStarHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
