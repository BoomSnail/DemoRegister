package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crooods.wd.dto.UserProfileDto;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.activities.MyAudioCacheActivity;
import com.knigego.nimo.demoregister.ui.activities.MyVideoCacheActivity;
import com.knigego.nimo.demoregister.ui.activities.PictureViewActivity;
import com.knigego.nimo.demoregister.ui.activities.UserInfoEditActivity;
import com.knigego.nimo.demoregister.ui.model.UserProfileItem;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.util.GlideCircleTransform;
import com.knigego.nimo.demoregister.util.MediaCacheDataHelper;
import com.knigego.nimo.demoregister.util.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人信息头部
 * Created by ThinkPad on 2017/4/2.
 */

public class UserInfoTypeHelper  extends ItemViewTypeHelperManager.ItemViewTypeHelper{

    public UserInfoTypeHelper(Context context, int layout) {
        super(context, layout);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new UserInfoHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        UserInfoHolder userInfoHolder = (UserInfoHolder) viewHolder;
        UserProfileItem userProfileItem = (UserProfileItem) data;
        final UserProfileDto userProfileDto = userProfileItem.getUserProfileDto();
        if (userProfileDto == null) {
            return;
        }
        //头像
        Glide.with(mContext).load(userProfileDto.getUserBaseDto().getAvator()).placeholder(R.drawable.rc_ic_def_msg_portrait)
                .transform(new GlideCircleTransform(mContext)).into(userInfoHolder.mImageAvatar);

        userInfoHolder.mImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(userProfileDto.getUserBaseDto().getAvator())) {
                    Intent intent = new Intent(mContext, PictureViewActivity.class);
                    intent.putExtra(PictureViewActivity.PICTURES,new String[]{userProfileDto.getUserBaseDto().getAvator()});
                    intent.putExtra(PictureViewActivity.POSITION,0);
                    mContext.startActivity(intent);
                }
            }
        });
        //舞台数量
        userInfoHolder.mTextStageNum.setText(String.valueOf(userProfileDto.getUserCountDto().getStagePosts()));
        //动态数量
        userInfoHolder.mTextMomentNum.setText(String.valueOf(userProfileDto.getUserCountDto().getMomentPosts()));
        //粉丝数量
        userInfoHolder.mTextFansNum.setText(String.valueOf(userProfileDto.getUserCountDto().getFollowers()));
        //个人简介
        userInfoHolder.mTextBref.setText(userProfileDto.getUserBaseDto().getIntro());
        //我的关注
        int followNum = userProfileDto.getUserCountDto().getFollowing() == null? 0:userProfileDto.getUserCountDto().getFollowing();
        userInfoHolder.mTextFollowNum.setText(String.format(mContext.getString(R.string.my_follow_num),String.valueOf(followNum)));
        //我的收藏
        int collectNum = userProfileDto.getUserCountDto().getFavorites() == null?0:userProfileDto.getUserCountDto().getFavorites();
        userInfoHolder.mTextFavoriteNum.setText(String.format(mContext.getString(R.string.my_favorite_num),String.valueOf(collectNum)));
        //我的视频缓存
        int videoNum = MediaCacheDataHelper.getMyVideoCacheNum();
        userInfoHolder.mTextVideoCacheNum.setText(String.format(mContext.getString(R.string.my_video_cache_num),videoNum));
        //我的音乐缓存
        int audioNum = MediaCacheDataHelper.getMyAudioCacheNum();
        userInfoHolder.mTextAudioCacheNum.setText(String.format(mContext.getString(R.string.my_audio_cache_num),audioNum));

        userInfoHolder.mLayoutFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,MyFollowsActivity.class));
            }
        });
        userInfoHolder.mLayoutFans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.gotoMyFans(mContext,userProfileDto.getUserBaseDto().getUserId());
            }
        });

        userInfoHolder.mLayoutFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.goToMyFavorite(mContext);
            }
        });

        userInfoHolder.mLayoutVideoCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,MyVideoCacheActivity.class));
            }
        });

        userInfoHolder.mLayoutAudioCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,MyAudioCacheActivity.class));
            }
        });

    }

    public class UserInfoHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.image_avatar)
        ImageView mImageAvatar;
        @Bind(R.id.text_stage_num)
        TextView mTextStageNum;
        @Bind(R.id.text_moment_num)
        TextView mTextMomentNum;
        @Bind(R.id.layout_comment)
        LinearLayout mLayoutComment;
        @Bind(R.id.text_fans_num)
        TextView mTextFansNum;
        @Bind(R.id.layout_fans)
        LinearLayout mLayoutFans;
        @Bind(R.id.layout_stage)
        LinearLayout mLayoutStage;
        @Bind(R.id.btn_edit_userinfo)
        Button mBtnEditUserinfo;
        @Bind(R.id.text_bref)
        TextView mTextBref;
        @Bind(R.id.text_follow_num)
        TextView mTextFollowNum;
        @Bind(R.id.layout_follow)
        LinearLayout mLayoutFollow;
        @Bind(R.id.text_favorite_num)
        TextView mTextFavoriteNum;
        @Bind(R.id.layout_favorite)
        LinearLayout mLayoutFavorite;
        @Bind(R.id.text_video_cache_num)
        TextView mTextVideoCacheNum;
        @Bind(R.id.layout_video_cache)
        LinearLayout mLayoutVideoCache;
        @Bind(R.id.text_audio_cache_num)
        TextView mTextAudioCacheNum;
        @Bind(R.id.layout_audio_cache)
        LinearLayout mLayoutAudioCache;
        public UserInfoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.btn_edit_userinfo)
        public  void goToUserEdit(){
            mContext.startActivity(new Intent(mContext, UserInfoEditActivity.class));
        }
    }
}
