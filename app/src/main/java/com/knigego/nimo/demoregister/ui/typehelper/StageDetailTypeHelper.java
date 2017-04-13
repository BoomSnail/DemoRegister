package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.dto.StringWrapper;
import com.crooods.wd.dto.ActionFavoriteCreateDto;
import com.crooods.wd.dto.ActionLikeCreateDto;
import com.crooods.wd.dto.post.MediaImageDto;
import com.crooods.wd.dto.post.StageDto;
import com.crooods.wd.dto.post.StageVideoDto;
import com.crooods.wd.enums.ClientTypeEnum;
import com.crooods.wd.enums.MediaTypeEnum;
import com.crooods.wd.enums.PostTypeEnum;
import com.crooods.wd.service.IActionStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.activities.StageDetailActivity;
import com.knigego.nimo.demoregister.ui.adapters.SuperStarAdapter;
import com.knigego.nimo.demoregister.ui.model.StageItem;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.util.AudioManager;
import com.knigego.nimo.demoregister.util.BrodcastHelper;
import com.knigego.nimo.demoregister.util.CommonUtils;
import com.knigego.nimo.demoregister.util.DeviceUtils;
import com.knigego.nimo.demoregister.util.GlideCircleTransform;
import com.knigego.nimo.demoregister.util.UIHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *
 * Created by ThinkPad on 2017/4/5.
 */

public class StageDetailTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper {
    private AudioManager mAudioManager;
    private int mType;

    public StageDetailTypeHelper(Context context, int layout, AudioManager audioManager, int type) {
        super(context, layout);
        mAudioManager = audioManager;
        this.mType = type;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new StageDetailViewHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {

        final StageDetailViewHolder holder = (StageDetailViewHolder) viewHolder;
        StageItem stageItem = (StageItem) data;
        final StageDto stageDto = stageItem.getStageDto();
        //头像
        Glide.with(mContext).load(stageDto.getUser().getAvator()).placeholder(R.drawable.rc_ic_def_msg_portrait)
                .transform(new GlideCircleTransform(mContext))
                .into(holder.mImageAvatar);
        holder.mImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.goToPersonalInfo(mContext, stageDto.getUser().getNickName(), stageDto.getUser().getId(), stageDto.getUser().getRole());
            }
        });
        holder.mTextName.setText(stageDto.getUser().getNickName());
        holder.mTextArea.setText(stageDto.getUser().getAddress());
        holder.mTextTime.setText(stageDto.getCreateDate() + "");
        holder.mTextContent.setText(stageDto.getContent());
        holder.mTextContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lineCount = holder.mTextContent.getLineCount();
                if (lineCount == 5) {
                    holder.mTextContent.setMaxLines(Integer.MAX_VALUE);
                    holder.mTextContent.setPadding(DeviceUtils.dipToPX(mContext, 10), 0, DeviceUtils.dipToPX(mContext, 10), 0);
                    holder.mTextContent.requestLayout();
                    holder.mImageTextUp.setVisibility(View.VISIBLE);
                } else {
                    holder.mTextContent.setMaxLines(5);
                    holder.mTextContent.setPadding(DeviceUtils.dipToPX(mContext, 10), DeviceUtils.dipToPX(mContext, 8), DeviceUtils.dipToPX(mContext, 10), 0);
                    holder.mTextContent.requestLayout();
                    holder.mImageTextUp.setVisibility(View.GONE);
                }
            }
        });

        if (stageDto.isLiked()) {
            holder.mImagePraise.setImageResource(R.drawable.icon_has_praise);
        } else {
            holder.mImagePraise.setImageResource(R.drawable.icon_praise_selector);
        }
        if (stageDto.isFavorited()) {
            holder.mImageCollect.setImageResource(R.drawable.icon_has_collect);
        } else {
            holder.mImageCollect.setImageResource(R.drawable.icon_collect_selector);
        }

        if (stageDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
            holder.mLayoutVideoCover.setVisibility(View.VISIBLE);
            holder.mLayoutImages.setVisibility(View.GONE);
            if (mType == AppConstants.TYPE_STAGE) {
                if (stageDto.getVideoData().getAudio() == null
                        || TextUtils.isEmpty(stageDto.getVideoData().getAudio().getUrl())) {//音乐为空
                    holder.mLayoutAudio.setVisibility(View.GONE);
                    holder.mViewAudioDivider.setVisibility(View.GONE);
                } else {
                    holder.mLayoutAudio.setVisibility(View.VISIBLE);
                    holder.mViewAudioDivider.setVisibility(View.VISIBLE);
                }
            } else {
                holder.mLayoutAudio.setVisibility(View.GONE);
                holder.mViewAudioDivider.setVisibility(View.GONE);
            }
            StageVideoDto stageVideoDto = stageDto.getVideoData();
            Glide.with(mContext).load(stageVideoDto.getCoverUrl())
                    .centerCrop().placeholder(R.color.image_default_color)
                    .error(R.drawable.error_image_fill_w)
                    .into(holder.mImageCover);
        } else if (stageDto.getMediaType().equals(MediaTypeEnum.IMAGE.getCode())) {
            holder.mLayoutVideoCover.setVisibility(View.GONE);
            holder.mLayoutAudio.setVisibility(View.GONE);
            holder.mViewAudioDivider.setVisibility(View.GONE);

            //显示图片
            if (stageDto.getImageData() != null) {
                holder.mLayoutImages.setVisibility(View.VISIBLE);
                setImageShow(holder.mLayoutImages, stageDto.getImageData().getImages());
            } else {
                holder.mLayoutImages.setVisibility(View.GONE);
            }
        }


        holder.mTextPraise.setText(String.format(mContext.getString(R.string.praise_count), stageDto.getLikes()));
        holder.mLayoutVideoCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stageDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
                    UIHelper.goToVideoPlayActivity(mContext, stageDto.getVideoData().getfUrl());
                    if (mAudioManager != null && mAudioManager.isPlayStatus()) {//暂停播放音乐
                        mAudioManager.pause();
                    }
                }
            }
        });
        holder.mLayoutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹起输入框
                if (mContext != null && mContext instanceof StageDetailActivity){
                    ((StageDetailActivity)mContext).editFocus();
                }
            }
        });
        holder.mImageMoreOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof StageDetailActivity) {
                    ((StageDetailActivity)mContext).reportOperate(stageDto);
                }
            }
        });
        holder.mLayoutPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praiseAction(stageDto);
            }
        });
        holder.mLayoutCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectAction(stageDto);
            }
        });
        
    }

    private void collectAction(final StageDto stageDto) {
        IActionStubService service = RetrofitUtil.createApi(IActionStubService.class);
        ActionFavoriteCreateDto createDto = new ActionFavoriteCreateDto();
        createDto.setPostId(stageDto.getId());
        if (mType == AppConstants.TYPE_STAGE) {
            createDto.setPostType(PostTypeEnum.STAGE.getCode());
        } else if (mType == AppConstants.TYPE_MOMENT) {
            createDto.setPostType(PostTypeEnum.MOMOENT.getCode());
        }
        createDto.setUserId(AppPref.getInstance().getUserId());
        service.favorite(AppPref.getInstance().getAccessToken(),mType == AppConstants.TYPE_STAGE?"stages":"moments",
                stageDto.getId(),createDto,new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext){
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            if (stageDto.isFavorited()) {
                                stageDto.setFavorited(false);
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
        final IActionStubService service = RetrofitUtil.createApi(IActionStubService.class);
        ActionLikeCreateDto likeCreateDto = new ActionLikeCreateDto();
        likeCreateDto.setPostId(stageDto.getId());
        likeCreateDto.setClient(ClientTypeEnum.ANDROID.getCode());
        if (mType == AppConstants.TYPE_STAGE) {
            likeCreateDto.setPostType(PostTypeEnum.STAGE.getCode());
        } else if (mType == AppConstants.TYPE_MOMENT) {
            likeCreateDto.setPostType(PostTypeEnum.MOMOENT.getCode());
        }
        likeCreateDto.setUserId(AppPref.getInstance().getUserId());
        service.like(AppPref.getInstance().getAccessToken(),mType == AppConstants.TYPE_STAGE?"stages":"moments",
                stageDto.getId(),likeCreateDto,new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext){
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            if (stageDto.isLiked()) {
                                stageDto.setLiked(false);
                                stageDto.setLikes(stageDto.getLikes() - 1);
                            } else {
                                stageDto.setLiked(true);
                                stageDto.setLikes(stageDto.getLikes()+1);
                            }
                            mBaseAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void setImageShow(LinearLayout layoutImages, List<MediaImageDto> images) {
        if (layoutImages.getChildCount() > 0) {
            return;
        }
        int marginBottom = DeviceUtils.dipToPX(mContext,8);
        for (int i = 0; i < images.size(); i++) {
            MediaImageDto imageDto = images.get(i);
            ImageView imageView = new ImageView(mContext);
            imageView.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(CommonUtils.getPictures(images),i,mContext));
            LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            pa.rightMargin = marginBottom;
//            pa.leftMargin = marginBottom;
//            pa.topMargin = marginBottom;
            pa.bottomMargin = marginBottom;
            imageView.setLayoutParams(pa);
            Glide.with(mContext).load(imageDto.getUrl()).into(imageView);
            layoutImages.addView(imageView);
        }
    }

    static class StageDetailViewHolder  extends RecyclerView.ViewHolder{
        @Bind(R.id.image_avatar)
        ImageView mImageAvatar;
        @Bind(R.id.text_name)
        TextView mTextName;
        @Bind(R.id.text_area)
        TextView mTextArea;
        @Bind(R.id.text_time)
        TextView mTextTime;
        @Bind(R.id.image_text_up)
        ImageView mImageTextUp;
        @Bind(R.id.text_content)
        TextView mTextContent;
        @Bind(R.id.image_cover)
        ImageView mImageCover;
        @Bind(R.id.layout_video_cover)
        RelativeLayout mLayoutVideoCover;
        @Bind(R.id.image_audio_operate)
        ImageView mImageAudioOperate;
        @Bind(R.id.seekbar_progress)
        SeekBar mSeekbarProgress;
        @Bind(R.id.text_total_time)
        TextView mTextTotalTime;
        @Bind(R.id.layout_audio)
        LinearLayout mLayoutAudio;
        @Bind(R.id.layout_images)
        LinearLayout mLayoutImages;
        @Bind(R.id.view_audio_divider)
        View mViewAudioDivider;
        @Bind(R.id.image_praise)
        ImageView mImagePraise;
        @Bind(R.id.text_praise)
        TextView mTextPraise;
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

        StageDetailViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
