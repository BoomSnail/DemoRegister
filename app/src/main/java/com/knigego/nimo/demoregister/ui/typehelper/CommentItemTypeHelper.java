package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crooods.wd.dto.version.ActionCommentDto;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.activities.StageDetailActivity;
import com.knigego.nimo.demoregister.ui.activities.SubCommentsActivity;
import com.knigego.nimo.demoregister.ui.model.CommentItem;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.util.GlideCircleTransform;
import com.knigego.nimo.demoregister.util.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ThinkPad on 2017/4/5.
 */

public class CommentItemTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper {
    private long mCommentId ;//-1为一级评论
    public CommentItemTypeHelper(Context context, int layoutId, long id) {
        super(context, layoutId);
        mCommentId = id;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new CommentItemViewHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        CommentItemViewHolder holder = (CommentItemViewHolder) viewHolder;
        CommentItem commentItem = (CommentItem) data;
        final ActionCommentDto actionCommentDto= commentItem.getActionCommentDto();
        Glide.with(mContext).load(actionCommentDto.getUserBaseDto().getAvator())
                .placeholder(R.drawable.rc_ic_def_msg_portrait)
                .transform(new GlideCircleTransform(mContext))
                .into(holder.mImageAvatar);

        holder.mTextName.setText(actionCommentDto.getUserBaseDto().getNickName());
        if (actionCommentDto.getReplyUserBaseDto() != null) {
            if (mCommentId == actionCommentDto.getReplyCommentId().longValue()
                    || mCommentId == -1) {
                holder.mTextContent.setText(actionCommentDto.getContent());
            } else {
                holder.mTextContent.setText(String.format(mContext.getString(R.string.reply_comment_tip),
                        actionCommentDto.getReplyUserBaseDto().getNickName()) + actionCommentDto.getContent());
            }
        } else {
            holder.mTextContent.setText(actionCommentDto.getContent());
        }

//        holder.mTextCommentTime.setText(actionCommentDto.getCommentTime() + "");
        holder.mTextSubCommentCount.setText(String.format(mContext.getString(R.string.sub_comment_count), actionCommentDto.getComments()));
        holder.mLayoutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof StageDetailActivity) {
                    ((StageDetailActivity)mContext).commentPopup(actionCommentDto);
                } else if (mContext instanceof SubCommentsActivity) {
                    ( (SubCommentsActivity)mContext).commentPopup(actionCommentDto);
                }
            }
        });
        holder.mImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.goToPersonalInfo(mContext,actionCommentDto.getUserBaseDto().getNickName(),actionCommentDto.getUserBaseDto().getUserId(),
                        actionCommentDto.getUserBaseDto().getRole());
            }
        });

    }

    static class CommentItemViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.image_avatar)
        ImageView mImageAvatar;
        @Bind(R.id.text_name)
        TextView mTextName;
        @Bind(R.id.text_content)
        TextView mTextContent;
        @Bind(R.id.text_comment_time)
        TextView mTextCommentTime;
        @Bind(R.id.text_sub_comment_count)
        TextView mTextSubCommentCount;
        @Bind(R.id.layout_comment)
        LinearLayout mLayoutComment;

        CommentItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
