package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.dto.StringWrapper;
import com.crooods.wd.dto.ActionFavoriteCreateDto;
import com.crooods.wd.dto.ActionLikeCreateDto;
import com.crooods.wd.dto.post.StageDto;
import com.crooods.wd.dto.post.StageImageDto;
import com.crooods.wd.dto.post.StageVideoDto;
import com.crooods.wd.enums.AuditStatusEnum;
import com.crooods.wd.enums.ClientTypeEnum;
import com.crooods.wd.enums.MediaTypeEnum;
import com.crooods.wd.enums.PostTypeEnum;
import com.crooods.wd.service.IActionStubService;
import com.crooods.wd.service.IFriendshipsStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.activities.MyFavoriteActivity;
import com.knigego.nimo.demoregister.ui.model.StageItem;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.util.BrodcastHelper;
import com.knigego.nimo.demoregister.util.GlideCircleTransform;
import com.knigego.nimo.demoregister.util.UIHelper;
import com.knigego.nimo.demoregister.view.ImageViewWrapper;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *
 * Created by ThinkPad on 2017/3/29.
 */

public class StageItemHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper {
    private boolean mIsMyStage = false;

    public StageItemHelper(Context context, int layout, boolean isMyStage) {

        super(context, layout);
        mIsMyStage = isMyStage;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new StageItemHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        StageItemHolder stageItemHolder = (StageItemHolder) viewHolder;
        StageItem stageItem = (StageItem) data;
        final StageDto stageDto = stageItem.getStageDto();
        if (stageDto.getStatus() == 1) {//舞台已删除
            stageItemHolder.mLayoutStage.setVisibility(View.GONE);
            stageItemHolder.mImageStageDelete.setVisibility(View.VISIBLE);
        } else {
            stageItemHolder.mLayoutStage.setVisibility(View.VISIBLE);
            stageItemHolder.mImageStageDelete.setVisibility(View.GONE);
        }
        //头像
        Glide.with(mContext).load(stageDto.getUser().getAvator()).placeholder(R.drawable.rc_ic_def_msg_portrait)
                .transform(new GlideCircleTransform(mContext)).into(stageItemHolder.mImageAvatar);
        //nickName
        stageItemHolder.mTextName.setText(stageDto.getUser().getNickName());
        //address
        stageItemHolder.mTextArea.setText(stageDto.getUser().getAddress());
        //intro
        stageItemHolder.mTextContent.setText(stageDto.getContent());

        //follow to unFollow
        if (stageDto.isFollowed()) {
            stageItemHolder.mBtnAttention.setBackgroundResource(R.drawable.btn_has_follow);
        } else {
            stageItemHolder.mBtnAttention.setBackgroundResource(R.drawable.btn_stage_attention_selector);
        }
        //点赞数量
        stageItemHolder.mTextPraiseInfo.setText(String.format(mContext.getString(R.string.praise_info),
                String.valueOf(stageDto.getLikes())));
        //评论数量
        stageItemHolder.mTextCommentInfo.setText(String.format(mContext.getString(R.string.comment_info),
                String.valueOf(stageDto.getComments())));

        //时间
        stageItemHolder.mTextTime.setText(String.valueOf(stageDto.getCreateDate()));

        if (stageDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
            stageItemHolder.mLayoutVideoCover.setVisibility(View.VISIBLE);
            stageItemHolder.mImageWrapper.setVisibility(View.GONE);
            StageVideoDto stageVideoDto = stageDto.getVideoData();

            Glide.with(mContext).load(stageVideoDto.getCoverUrl())
                    .centerCrop().placeholder(R.color.image_default_color)
                    .error(R.drawable.error_image_fill_w).into(stageItemHolder.mImageCover);
        } else if (stageDto.getMediaType().equals(MediaTypeEnum.IMAGE.getCode())) {
            stageItemHolder.mLayoutVideoCover.setVisibility(View.GONE);
            stageItemHolder.mImageWrapper.setVisibility(View.VISIBLE);
            StageImageDto stageImageDto = stageDto.getImageData();
            stageItemHolder.mImageWrapper.setImages(stageImageDto.getImages());
        } else {
            stageItemHolder.mLayoutVideoCover.setVisibility(View.GONE);
            stageItemHolder.mImageWrapper.setVisibility(View.GONE);
        }
        if (stageDto.isLiked()) {
            stageItemHolder.mImagePraise.setImageResource(R.drawable.icon_has_praise);
        } else {
            stageItemHolder.mImagePraise.setImageResource(R.drawable.icon_praise_selector);
        }
        //收藏了
        if (stageDto.isFavorited()) {
            stageItemHolder.mImageCollect.setImageResource(R.drawable.icon_has_collect);
        } else {
            stageItemHolder.mImageCollect.setImageResource(R.drawable.icon_collect_selector);
        }

        if (mIsMyStage) {
            stageItemHolder.mLayoutOperate.setVisibility(View.GONE);
            stageItemHolder.mBtnAttention.setVisibility(View.GONE);
            if (stageDto.getAuditStatus().equals(AuditStatusEnum.PASSED.getCode())) {//审核通过
                stageItemHolder.mTextAudit.setVisibility(View.GONE);
            } else {
                stageItemHolder.mTextAudit.setVisibility(View.VISIBLE);
            }
        } else {
            stageItemHolder.mLayoutOperate.setVisibility(View.VISIBLE);
            if (stageDto.getUser().getId().longValue() == AppPref.getInstance().getUserId()) {
                stageItemHolder.mBtnAttention.setVisibility(View.GONE);
                if (stageDto.getAuditStatus().equals(AuditStatusEnum.PASSED.getCode())) {
                    stageItemHolder.mTextAudit.setVisibility(View.GONE);
                } else {
                    stageItemHolder.mTextAudit.setVisibility(View.VISIBLE);
                }
            } else {
                stageItemHolder.mBtnAttention.setVisibility(View.VISIBLE);
            }
        }

        stageItemHolder.mImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到个人信息
                UIHelper.goToPersonalInfo(mContext, stageDto.getUser().getNickName(), stageDto.getUser().getId(),
                        stageDto.getUser().getRole());
            }
        });

        //follow or unFollow
        stageItemHolder.mBtnAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stageDto.isFollowed()) {
                    cancelFollow(stageDto);
                } else {
                    follow(stageDto);
                }
            }
        });

        //video
        stageItemHolder.mLayoutVideoCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stageDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
                    UIHelper.goToVideoPlayActivity(mContext, stageDto.getVideoData().getfUrl());
                }
            }
        });

        //praise or unPraise
        stageItemHolder.mLayoutPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praiseAction(stageDto);
            }
        });
        //collect or unCollect
        stageItemHolder.mLayoutCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectAction(stageDto);
            }
        });

        stageItemHolder.mLayoutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转至详情页
                UIHelper.goToDetail(mContext, AppConstants.TYPE_STAGE, stageDto, true);
            }
        });

        //more
        stageItemHolder.mImageMoreOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatePopup(stageDto);
            }
        });

        stageItemHolder.mLayoutStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.goToDetail(mContext, AppConstants.TYPE_STAGE, stageDto, false);
            }
        });

        stageItemHolder.mImageStageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertCancelCollect(stageDto);
            }
        });
    }

    private void alertCancelCollect(final StageDto stageDto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.content_delete);
        builder.setPositiveButton(R.string.delete_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                collectAction(stageDto);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.delete_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void operatePopup(final StageDto stageDto) {
        DialogPlus dialogPlus = DialogPlus.newDialog(mContext)
                .setContentHolder(new ViewHolder(R.layout.poopup_stage_menu))
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        String imageUrl = "";
                        String title = String.format(mContext.getString(R.string.share_dance), stageDto.getUser().getNickName());

                        if (stageDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
                            imageUrl = stageDto.getVideoData().getCoverUrl();
                            title = String.format(mContext.getString(R.string.share_dance),
                                    stageDto.getUser().getNickName());
                        } else if (stageDto.getMediaType().equals(MediaTypeEnum.IMAGE.getCode())) {
                            imageUrl = stageDto.getImageData().getImages().get(0).getUrl();
                            title = String.format(mContext.getString(R.string.share_picture), stageDto.getUser().getNickName());

                        }
                        switch (view.getId()) {
                            case R.id.text_cache_video:

                                break;
                            case R.id.text_cache_audio:

                                break;

                            case R.id.layout_share_we_chat_friend:

                                break;
                            case R.id.layout_share_wechat_circle:
                                break;
                            case R.id.layout_share_qq:
                                break;
                            case R.id.layout_share_weibo:
                                break;

                            case R.id.layout_share_facebook:
                                break;

                            case R.id.cancel:
                                break;
                        }

                        dialog.dismiss();
                    }
                })
                .create();
        View view = dialogPlus.getHolderView();
        TextView cacheVideoText = (TextView) view.findViewById(R.id.text_cache_video);
        View videoDivider = view.findViewById(R.id.view_cache_video_divider);
        TextView cacheAudioText = (TextView) view.findViewById(R.id.text_cache_audio);
        View audioDivider = view.findViewById(R.id.view_cache_audio_divider);

        if (stageDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
            cacheVideoText.setVisibility(View.VISIBLE);
            videoDivider.setVisibility(View.VISIBLE);
            if (stageDto.getVideoData().getAudio() == null ||
                    TextUtils.isEmpty(stageDto.getVideoData().getAudio().getUrl())) {
                cacheAudioText.setVisibility(View.GONE);
                audioDivider.setVisibility(View.GONE);
            } else {
                cacheAudioText.setVisibility(View.VISIBLE);
                audioDivider.setVisibility(View.VISIBLE);
            }
        } else {
            cacheVideoText.setVisibility(View.GONE);
            videoDivider.setVisibility(View.GONE);
            cacheAudioText.setVisibility(View.GONE);
            audioDivider.setVisibility(View.GONE);
        }
        dialogPlus.show();

    }

    private void collectAction(final StageDto stageDto) {
        IActionStubService stubService = RetrofitUtil.createApi(IActionStubService.class);
        ActionFavoriteCreateDto favoriteCreateDto = new ActionFavoriteCreateDto();
        favoriteCreateDto.setPostId(stageDto.getId());
        favoriteCreateDto.setPostType(PostTypeEnum.STAGE.getCode());
        favoriteCreateDto.setUserId(AppPref.getInstance().getUserId());

        stubService.favorite(AppPref.getInstance().getAccessToken(), "stages", stageDto.getId(),
                favoriteCreateDto, new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            if (stageDto.isFavorited()) {
                                stageDto.setFavorited(false);
                                if (mContext instanceof MyFavoriteActivity) {
                                    List<ItemViewTypeHelperManager.ItemViewData> itemViewDatas = mBaseAdapter.getItemViewDatas();
                                    for (int i = 0; i < itemViewDatas.size(); i++) {
                                        ItemViewTypeHelperManager.ItemViewData itemViewData = itemViewDatas.get(i);
                                        if (itemViewData instanceof StageItem) {
                                            StageItem stageItem = (StageItem) itemViewData;
                                            if (stageItem.getStageDto().getId() == stageDto.getId()) {
                                                itemViewDatas.remove(i);
                                                ((MyFavoriteActivity) mContext).deleteStatus(stageDto.getId());
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                stageDto.setFavorited(true);
                            }
                            mBaseAdapter.notifyDataSetChanged();

                            BrodcastHelper.sendUserInfoUpdateAction(mContext);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                    }
                });

    }


    private void praiseAction(final StageDto stageDto) {
        IActionStubService stubService = RetrofitUtil.createApi(IActionStubService.class);
        ActionLikeCreateDto actionLikeCreateDto = new ActionLikeCreateDto();
        actionLikeCreateDto.setPostId(stageDto.getId());
        actionLikeCreateDto.setClient(ClientTypeEnum.ANDROID.getCode());
        actionLikeCreateDto.setPostType(PostTypeEnum.STAGE.getCode());
        actionLikeCreateDto.setUserId(AppPref.getInstance().getUserId());

        stubService.like(AppPref.getInstance().getAccessToken(),"stages",stageDto.getId(),actionLikeCreateDto,
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext){
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            if (stageDto.isLiked()) {
                                stageDto.setLiked(false);
                                stageDto.setLikes(stageDto.getLikes() - 1);
                            } else {
                                stageDto.setLiked(true);
                                stageDto.setLikes(stageDto.getLikes() + 1);
                            }

                            mBaseAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                    }
                });
    }

    private void follow(final StageDto stageDto) {
        IFriendshipsStubService service = RetrofitUtil.createApi(IFriendshipsStubService.class);
        service.friendsCancel(AppPref.getInstance().getAccessToken(), stageDto.getUser().getId(),
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            setFollowStatus(stageDto.getUser().getId(), true);
                            //广播
                            BrodcastHelper.sendUserInfoUpdateAction(mContext);
                        }
                    }
                });
    }

    private void cancelFollow(final StageDto stageDto) {
        IFriendshipsStubService service = RetrofitUtil.createApi(IFriendshipsStubService.class);
        service.friendsCreate(AppPref.getInstance().getAccessToken(), stageDto.getUser().getId(),
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            setFollowStatus(stageDto.getUser().getId(), false);
                            BrodcastHelper.sendUserInfoUpdateAction(mContext);
                        }
                    }
                });
    }

    private void setFollowStatus(Long userId, boolean follow) {
        for (int i = 0; i < mBaseAdapter.getListData().size(); i++) {
            ItemViewTypeHelperManager.ItemViewData itemViewData = mBaseAdapter.getListData().get(i);
            if (itemViewData instanceof  StageItem) {
                StageDto stageItem = ((StageItem) itemViewData).getStageDto();
                if (stageItem.getUser().getId().longValue() == userId) {
                    stageItem.setFollowed(follow);
                }
            }
        }

        mBaseAdapter.notifyDataSetChanged();

    }

    public class StageItemHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image_avatar)
        ImageView mImageAvatar;
        @Bind(R.id.text_name)
        TextView mTextName;
        @Bind(R.id.text_area)
        TextView mTextArea;
        @Bind(R.id.text_content)
        TextView mTextContent;
        @Bind(R.id.layout_user)
        LinearLayout mLayoutUser;
        @Bind(R.id.btn_attention)
        Button mBtnAttention;
        @Bind(R.id.text_audit)
        TextView mTextAudit;
        @Bind(R.id.image_cover)
        ImageView mImageCover;
        @Bind(R.id.layout_video_cover)
        RelativeLayout mLayoutVideoCover;
        @Bind(R.id.image_wrapper)
        ImageViewWrapper mImageWrapper;
        @Bind(R.id.text_praise_info)
        TextView mTextPraiseInfo;
        @Bind(R.id.text_comment_info)
        TextView mTextCommentInfo;
        @Bind(R.id.text_time)
        TextView mTextTime;
        @Bind(R.id.image_praise)
        ImageView mImagePraise;
        @Bind(R.id.layout_praise)
        LinearLayout mLayoutPraise;
        @Bind(R.id.image_collect)
        ImageView mImageCollect;
        @Bind(R.id.layout_collect)
        LinearLayout mLayoutCollect;
        @Bind(R.id.image_comment)
        ImageView mImageComment;
        @Bind(R.id.layout_comment)
        LinearLayout mLayoutComment;
        @Bind(R.id.image_more_operate)
        ImageView mImageMoreOperate;
        @Bind(R.id.layout_operate)
        RelativeLayout mLayoutOperate;
        @Bind(R.id.layout_stage)
        LinearLayout mLayoutStage;
        @Bind(R.id.image_stage_delete)
        ImageView mImageStageDelete;

        public StageItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
