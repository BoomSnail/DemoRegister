package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.dto.StringWrapper;
import com.crooods.wd.dto.post.StageDto;
import com.crooods.wd.enums.MediaTypeEnum;
import com.crooods.wd.service.IFriendshipsStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.adapters.SuperStarAdapter;
import com.knigego.nimo.demoregister.ui.model.DancerItem;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.util.BrodcastHelper;
import com.knigego.nimo.demoregister.util.CommonUtils;
import com.knigego.nimo.demoregister.util.GlideCircleTransform;
import com.knigego.nimo.demoregister.util.UIHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by ThinkPad on 2017/4/13.
 */

public class DancerItemTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper {

    public DancerItemTypeHelper(Context context, int layout) {
        super(context, layout);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {

        return new DancerViewHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        DancerViewHolder holder = (DancerViewHolder) viewHolder;
        final DancerItem dancerItem = (DancerItem) data;
        setMediaShow(holder,dancerItem);
        //头像
        Glide.with(mContext).load(dancerItem.getDancerDto().getUser().getAvator())
                .placeholder(R.drawable.rc_ic_def_msg_portrait)
                .transform(new GlideCircleTransform(mContext))
                .into(holder.mImageAvatar);
        //性别
        String sex = dancerItem.getDancerDto().getUserDetail().getSex();
        if (!TextUtils.isEmpty(sex) && sex.equals("Girl")) {
            holder.mImageSex.setImageResource(R.drawable.icon_sex_girl);
        } else {
            holder.mImageSex.setImageResource(R.drawable.icon_sex_boy);
        }
        //nickName
        holder.mTextName.setText(dancerItem.getDancerDto().getUser().getNickName());
        //地址
        holder.mTextAddress.setText(dancerItem.getDancerDto().getUser().getAddress());
        //自我介绍
        holder.mTextBrief.setText(dancerItem.getDancerDto().getUser().getIntro());
        //关注与取消关注
        if (dancerItem.getDancerDto().isFollowed()) {
            holder.mImageFollow.setImageResource(R.drawable.icon_user_has_follow);
            holder.mTextFollow.setText(R.string.cancel_follow);
        } else {
            holder.mImageFollow.setImageResource(R.drawable.icon_user_follow);
            holder.mTextFollow.setText(R.string.ta_follow);
        }
        holder.mLayoutFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dancerItem.getDancerDto().getUser().getId() == AppPref.getInstance().getUserId()) {
                    Toast.makeText(mContext,R.string.cant_follow_myself,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dancerItem.getDancerDto().isFollowed()) {
                    cancelFollow(dancerItem);
                } else {
                    follow(dancerItem);
                }
            }
        });
        if (dancerItem.getDancerDto().getFriendsStatus().equals("1")||dancerItem.getDancerDto().getFriendsStatus().equals("2")) {
            holder.mTextFriend.setText(R.string.is_a_friend);
            holder.mImageFriend.setImageResource(R.drawable.icon_user_follow);
            //TODO
        } else {
            holder.mTextFriend.setText(R.string.add_friend);
            holder.mImageFriend.setImageResource(R.drawable.icon_user_add_friend);
            holder.mLayoutFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFriendAction(dancerItem.getDancerDto().getUser().getId());
                }
            });
        }
    }

    private void addFriendAction(Long id) {
    }

    private void follow(final DancerItem dancerItem) {
        IFriendshipsStubService service = RetrofitUtil.createApi(IFriendshipsStubService.class);
        service.friendsCreate(AppPref.getInstance().getAccessToken(), dancerItem.getDancerDto().getUser().getId(),
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            dancerItem.getDancerDto().setFollowed(true);
                            mBaseAdapter.notifyDataSetChanged();

                            BrodcastHelper.sendUserInfoUpdateAction(mContext);
                        }
                    }
                });
    }

    private void cancelFollow(final DancerItem dancerItem) {
        IFriendshipsStubService service = RetrofitUtil.createApi(IFriendshipsStubService.class);
        service.friendsCancel(AppPref.getInstance().getAccessToken(), dancerItem.getDancerDto().getUser().getId(),
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if(stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)){
                            dancerItem.getDancerDto().setFollowed(false);
                            mBaseAdapter.notifyDataSetChanged();

                            BrodcastHelper.sendUserInfoUpdateAction(mContext);
                        }
                    }
                });
    }

    private void setMediaShow(DancerViewHolder holder, DancerItem dancerItem) {
        List<StageDto> stageDtos = dancerItem.getDancerDto().getStages();
        if (stageDtos == null || stageDtos.size() == 0) {
            setNotContent(holder.mLayoutMedia1, holder.mImage1, holder.mImagePlay1);
            setNotContent(holder.mLayoutMedia2, holder.mImage2, holder.mImagePlay2);
            setNotContent(holder.mLayoutMedia3, holder.mImage3, holder.mImagePlay3);
            setNotContent(holder.mLayoutMedia4, holder.mImage4, holder.mImagePlay4);
        } else {
            int count= stageDtos.size();
            switch (count){
                case 1:
                    loadMedia(holder.mLayoutMedia1,holder.mImage1,holder.mImagePlay1,dancerItem,0);
                    setNotContent(holder.mLayoutMedia2,holder.mImage2,holder.mImagePlay2);
                    setNotContent(holder.mLayoutMedia3,holder.mImage3,holder.mImagePlay3);
                    setNotContent(holder.mLayoutMedia4,holder.mImage4,holder.mImagePlay4);
                    break;
                case 2:
                    loadMedia(holder.mLayoutMedia1,holder.mImage1,holder.mImagePlay1,dancerItem,0);
                    loadMedia(holder.mLayoutMedia2,holder.mImage2,holder.mImagePlay2,dancerItem,1);
                    setNotContent(holder.mLayoutMedia3,holder.mImage3,holder.mImagePlay3);
                    setNotContent(holder.mLayoutMedia4,holder.mImage4,holder.mImagePlay4);

                    break;
                case 3:
                    loadMedia(holder.mLayoutMedia1,holder.mImage1,holder.mImagePlay1,dancerItem,0);
                    loadMedia(holder.mLayoutMedia2,holder.mImage2,holder.mImagePlay2,dancerItem,1);
                    loadMedia(holder.mLayoutMedia3,holder.mImage3,holder.mImagePlay3,dancerItem,2);

                    setNotContent(holder.mLayoutMedia4,holder.mImage4,holder.mImagePlay4);

                    break;

                case 4:
                    loadMedia(holder.mLayoutMedia1,holder.mImage1,holder.mImagePlay1,dancerItem,0);
                    loadMedia(holder.mLayoutMedia2,holder.mImage2,holder.mImagePlay2,dancerItem,1);
                    loadMedia(holder.mLayoutMedia3,holder.mImage3,holder.mImagePlay3,dancerItem,2);
                    loadMedia(holder.mLayoutMedia4,holder.mImage4,holder.mImagePlay4,dancerItem,3);

                    break;
            }
        }
    }

    private void loadMedia(RelativeLayout layout, ImageView imageView, ImageView playImageView, DancerItem dancerItem, int i) {
        layout.setEnabled(true);
        final StageDto stageDto = dancerItem.getDancerDto().getStages().get(i);
        if (stageDto.getMediaType().equals(MediaTypeEnum.IMAGE.getCode())) {
            playImageView.setVisibility(View.GONE);
            loadImage(imageView, stageDto.getImageData().getImages().get(0).getUrl(), R.color.image_default_color);
            layout.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(
                    CommonUtils.getPictures(stageDto.getImageData().getImages()), 0, mContext));
        } else {
            playImageView.setVisibility(View.VISIBLE);
            loadImage(imageView,stageDto.getVideoData().getCoverUrl(),R.color.image_default_color);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.goToVideoPlayActivity(mContext,stageDto.getVideoData().getfUrl());
                }
            });
        }
    }

    private void loadImage(ImageView imageView, String url, int defaultImage) {
        Glide.with(mContext).load(url).centerCrop()
                .placeholder(defaultImage)
                .error(R.drawable.error_image_a_half_w)
                .into(imageView);
    }

    //内容为空
    private void setNotContent(RelativeLayout layout, ImageView imageView, ImageView playImageView) {
        imageView.setImageResource(R.drawable.super_star_not_content_bg);
        playImageView.setVisibility(View.GONE);
        layout.setEnabled(false);
    }


    static class DancerViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_1)
        ImageView mImage1;
        @Bind(R.id.image_play_1)
        ImageView mImagePlay1;
        @Bind(R.id.layout_media_1)
        RelativeLayout mLayoutMedia1;
        @Bind(R.id.image_2)
        ImageView mImage2;
        @Bind(R.id.image_play_2)
        ImageView mImagePlay2;
        @Bind(R.id.layout_media_2)
        RelativeLayout mLayoutMedia2;
        @Bind(R.id.image_3)
        ImageView mImage3;
        @Bind(R.id.image_play_3)
        ImageView mImagePlay3;
        @Bind(R.id.layout_media_3)
        RelativeLayout mLayoutMedia3;
        @Bind(R.id.image_4)
        ImageView mImage4;
        @Bind(R.id.image_play_4)
        ImageView mImagePlay4;
        @Bind(R.id.layout_media_4)
        RelativeLayout mLayoutMedia4;
        @Bind(R.id.image_avatar)
        ImageView mImageAvatar;
        @Bind(R.id.image_sex)
        ImageView mImageSex;
        @Bind(R.id.text_name)
        TextView mTextName;
        @Bind(R.id.text_address)
        TextView mTextAddress;
        @Bind(R.id.text_brief)
        TextView mTextBrief;
        @Bind(R.id.image_friend)
        ImageView mImageFriend;
        @Bind(R.id.text_friend)
        TextView mTextFriend;
        @Bind(R.id.layout_friend)
        LinearLayout mLayoutFriend;
        @Bind(R.id.image_follow)
        ImageView mImageFollow;
        @Bind(R.id.text_follow)
        TextView mTextFollow;
        @Bind(R.id.layout_follow)
        LinearLayout mLayoutFollow;
        @Bind(R.id.layout_dancer_item)
        LinearLayout mLayoutDancerItem;

        DancerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
