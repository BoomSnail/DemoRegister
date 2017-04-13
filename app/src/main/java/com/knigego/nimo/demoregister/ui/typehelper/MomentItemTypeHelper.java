package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.dto.StringWrapper;
import com.crooods.wd.dto.ActionFavoriteCreateDto;
import com.crooods.wd.dto.post.MomentDto;
import com.crooods.wd.dto.post.StageDto;
import com.crooods.wd.enums.MediaTypeEnum;
import com.crooods.wd.enums.PostTypeEnum;
import com.crooods.wd.service.IActionStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.activities.MyFavoriteActivity;
import com.knigego.nimo.demoregister.ui.adapters.SuperStarAdapter;
import com.knigego.nimo.demoregister.ui.model.MomentItem;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.util.BrodcastHelper;
import com.knigego.nimo.demoregister.util.CommonUtils;
import com.knigego.nimo.demoregister.util.DeviceUtils;
import com.knigego.nimo.demoregister.util.GlideCircleTransform;
import com.knigego.nimo.demoregister.util.GlideRoundTransform;
import com.knigego.nimo.demoregister.util.UIHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by ThinkPad on 2017/4/2.
 */

public class MomentItemTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper {
    private int imageWidth;

    public MomentItemTypeHelper(Context context, int layout) {
        super(context, layout);

        imageWidth = (DeviceUtils.getScreenWidth(mContext) - DeviceUtils.dipToPX(context, 12))/2;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new MomentViewHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        MomentViewHolder holder = (MomentViewHolder) viewHolder;
        MomentItem momentItem = (MomentItem) data;
        final MomentDto momentDto = momentItem.getMomentDto();
        if (momentDto.getStatus() == 1) {//动态已删除
            holder.mLayoutMoment.setVisibility(View.GONE);
            holder.mImageMomentDelete.setVisibility(View.VISIBLE);
        } else {
            holder.mLayoutMoment.setVisibility(View.VISIBLE);
            holder.mImageMomentDelete.setVisibility(View.GONE);
        }

        //内容为空和不为空的高度
        if (TextUtils.isEmpty(momentDto.getContent())) {
            ViewGroup.LayoutParams params = holder.mImageMoment.getLayoutParams();
            params.width = imageWidth;
            params.height = DeviceUtils.dipToPX(mContext, 200);
            holder.mImageMoment.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = holder.mImageMoment.getLayoutParams();
            params.width = imageWidth;
            params.height = DeviceUtils.dipToPX(mContext, 165);
            holder.mImageMoment.setVisibility(View.VISIBLE);
        }
        if (momentDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
            holder.mImagePlayer.setVisibility(View.VISIBLE);
            holder.mLayoutImage.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(momentDto.getVideoData().getCoverUrl())
                    .centerCrop().override(imageWidth,
                    TextUtils.isEmpty(momentDto.getContent()) ? DeviceUtils.dipToPX(mContext, 200) : DeviceUtils.dipToPX(mContext, 165))
                    .placeholder(R.color.image_default_color)
                    .error(TextUtils.isEmpty(momentDto.getContent()) ? R.drawable.error_image_moment_long : R.drawable.error_image_moment_short)
                    .bitmapTransform(new GlideRoundTransform(mContext))
                    .into(holder.mImageMoment);
            holder.mLayoutUserInfo1.setVisibility(View.GONE);
            holder.mLayoutUserInfo2.setVisibility(View.VISIBLE);
            holder.mTextName2.setText(momentDto.getUser().getNickName());
            Glide.with(mContext).load(momentDto.getUser().getAvator())
                    .placeholder(R.drawable.rc_ic_def_msg_portrait)
                    .into(holder.mImageAvatar2);
        } else {
            holder.mImagePlayer.setVisibility(View.GONE);
            if (momentDto.getImageData() == null || momentDto.getImageData().getImages() == null
                    || momentDto.getImageData().getImages().size() == 0) {
                holder.mLayoutUserInfo1.setVisibility(View.VISIBLE);
                holder.mLayoutUserInfo2.setVisibility(View.GONE);
                holder.mLayoutImage.setVisibility(View.GONE);
                holder.mTextContent.setMaxLines(4);
                holder.mTextName1.setText(momentDto.getUser().getNickName());
                Glide.with(mContext).load(momentDto.getUser().getAvator())
                        .transform(new GlideRoundTransform(mContext))
                        .placeholder(R.drawable.rc_ic_def_msg_portrait)
                        .into(holder.mImageAvatar1);
            }else {

                holder.mLayoutUserInfo1.setVisibility(View.GONE);
                holder.mLayoutUserInfo2.setVisibility(View.VISIBLE);
                holder.mLayoutImage.setVisibility(View.VISIBLE);
                holder.mTextContent.setMaxLines(2);
                Glide.with(mContext).load(momentDto.getImageData().getImages().get(0).getUrl())
                        .centerCrop()
                        .override(imageWidth,TextUtils.isEmpty(momentDto.getContent())?
                                DeviceUtils.dipToPX(mContext, 200):DeviceUtils.dipToPX(mContext, 165))
                        .placeholder(R.color.image_default_color)
                        .error(TextUtils.isEmpty(momentDto.getContent())?
                                R.drawable.error_image_moment_long:R.drawable.error_image_moment_short)
                        .into(holder.mImageMoment);
                holder.mTextName2.setText(momentDto.getUser().getNickName());
                Glide.with(mContext).load(momentDto.getUser().getAvator())
                        .transform(new GlideCircleTransform(mContext))
                        .placeholder(R.drawable.rc_ic_def_msg_portrait)
                        .into(holder.mImageAvatar2);
            }
        }

        holder.mTextContent.setText(momentDto.getContent());
        holder.mTextTime.setText(momentDto.getCreateDate() + "");
        holder.mTextPraiseNum.setText(String.valueOf(momentDto.getLikes()));
        holder.mTextCommentNum.setText(String.valueOf(momentDto.getComments()));
        holder.mLayoutMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String jsonstring = JSON.toJSONString(momentDto);
                StageDto stageDto = JSON.parseObject(jsonstring,StageDto.class);
                UIHelper.goToDetail(mContext, AppConstants.TYPE_MOMENT,stageDto,false);
            }

        });

        holder.mImageMomentDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertCancelCollect(momentDto);
            }
        });

        //视频播放
        if (momentDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
            holder.mImagePlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.goToVideoPlayActivity(mContext, momentDto.getVideoData().getfUrl());
                }
            });
        } else if (momentDto.getMediaType().equals(MediaTypeEnum.IMAGE.getCode())) {
            if (momentDto.getImageData() != null) {
                holder.mLayoutImage.
                        setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(CommonUtils.getPictures(momentDto.getImageData().getImages()), 0, mContext));
            } else {
                holder.mLayoutImage.setOnClickListener(null);
            }
        } else {
            holder.mLayoutImage.setOnClickListener(null);
        }

    }

    private void alertCancelCollect(final MomentDto momentDto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.delete_confirm);
        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                collectAction(momentDto);
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

    private void collectAction(final MomentDto momentDto) {
        IActionStubService service = RetrofitUtil.createApi(IActionStubService.class);
        ActionFavoriteCreateDto dto = new ActionFavoriteCreateDto();
        dto.setPostId(momentDto.getId());
        dto.setPostType(PostTypeEnum.MOMOENT.getCode());
        dto.setUserId(AppPref.getInstance().getUserId());
        service.favorite(AppPref.getInstance().getAccessToken(),"moments",momentDto.getId(),
                dto,new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext){
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (momentDto.isFavorited()) {
                            momentDto.setFavorited(false);
                            if (mContext instanceof MyFavoriteActivity) {
                                List<ItemViewTypeHelperManager.ItemViewData> itemViewDatas = mBaseAdapter.getListData();
                                for (int i = 0; i < itemViewDatas.size(); i++) {
                                    ItemViewTypeHelperManager.ItemViewData itemViewData = itemViewDatas.get(i);
                                    if (itemViewData instanceof  MomentItem) {
                                        MomentItem momentItem = (MomentItem) itemViewData;
                                        if (momentItem.getMomentDto().getId() == momentDto.getId()) {
                                            itemViewDatas.remove(i);
                                            ((MyFavoriteActivity)mContext).deleteMoment(momentDto.getId());
                                            break;
                                        }
                                    }
                                }
                            }else {
                                momentDto.setFavorited(true);
                            }
                            mBaseAdapter.notifyDataSetChanged();

                            BrodcastHelper.sendUserInfoUpdateAction(mContext);
                        }
                    }
                });
    }

    public class MomentViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image_moment)
        ImageView mImageMoment;
        @Bind(R.id.image_player)
        ImageView mImagePlayer;
        @Bind(R.id.layout_image)
        RelativeLayout mLayoutImage;
        @Bind(R.id.image_avatar_1)
        ImageView mImageAvatar1;
        @Bind(R.id.text_name_1)
        TextView mTextName1;
        @Bind(R.id.layout_user_info_1)
        LinearLayout mLayoutUserInfo1;
        @Bind(R.id.text_content)
        TextView mTextContent;
        @Bind(R.id.image_avatar_2)
        ImageView mImageAvatar2;
        @Bind(R.id.text_name_2)
        TextView mTextName2;
        @Bind(R.id.layout_user_info_2)
        LinearLayout mLayoutUserInfo2;
        @Bind(R.id.text_time)
        TextView mTextTime;
        @Bind(R.id.text_praise_num)
        TextView mTextPraiseNum;
        @Bind(R.id.text_comment_num)
        TextView mTextCommentNum;
        @Bind(R.id.layout_moment)
        LinearLayout mLayoutMoment;
        @Bind(R.id.image_moment_delete)
        ImageView mImageMomentDelete;

        public MomentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
