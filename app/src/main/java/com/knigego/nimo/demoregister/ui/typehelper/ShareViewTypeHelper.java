package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.crooods.wd.dto.post.StageDto;
import com.crooods.wd.enums.MediaTypeEnum;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.model.ShareInfo;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.util.ShareUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ThinkPad on 2017/4/5.
 */

public class ShareViewTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper {
    public ShareViewTypeHelper(Context context, int layout) {
        super(context, layout);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ShareViewHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        ShareViewHolder shareViewHolder = (ShareViewHolder) viewHolder;
        ShareInfo shareInfo = (ShareInfo) data;
        shareViewHolder.mLayoutShareWechatFriend.setOnClickListener(new MyClickListener(shareInfo));
        shareViewHolder.mLayoutShareWechatCircle.setOnClickListener(new MyClickListener(shareInfo));
        shareViewHolder.mLayoutShareFacebook.setOnClickListener(new MyClickListener(shareInfo));
        shareViewHolder.mLayoutShareWeibo.setOnClickListener(new MyClickListener(shareInfo));
        shareViewHolder.mLayoutShareQq.setOnClickListener(new MyClickListener(shareInfo));
    }

    static class ShareViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.layout_share_wechat_friend)
        LinearLayout mLayoutShareWechatFriend;
        @Bind(R.id.layout_share_wechat_circle)
        LinearLayout mLayoutShareWechatCircle;
        @Bind(R.id.layout_share_facebook)
        LinearLayout mLayoutShareFacebook;
        @Bind(R.id.layout_share_weibo)
        LinearLayout mLayoutShareWeibo;
        @Bind(R.id.layout_share_qq)
        LinearLayout mLayoutShareQq;

        ShareViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private class MyClickListener implements View.OnClickListener {
        private ShareInfo mShareInfo;

        public MyClickListener(ShareInfo shareInfo) {
            mShareInfo = shareInfo;
        }

        @Override
        public void onClick(View v) {
            StageDto stageDto = mShareInfo.getStageDto();
            String nickName = stageDto.getUser().getNickName();
            String content = stageDto.getContent();
            long id = stageDto.getId();
            int type = mShareInfo.getType();

            String shareUrl = ShareUtils.getShareUrl(type, id);
            String imageUrl = "";
            String title = String.format(mContext.getString(R.string.share_dance), nickName);
            if (type == AppConstants.TYPE_STAGE) {
                if (stageDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {

                    title = String.format(mContext.getString(R.string.share_dance), nickName);
                } else if (stageDto.getMediaType().equals(MediaTypeEnum.IMAGE.getCode())) {
                    if (stageDto.getImageData().getImages() != null && stageDto.getImageData().getImages().size() > 0) {
                        title = String.format(mContext.getString(R.string.share_picture), nickName);
                    }
                }
            } else {
                title = String.format(mContext.getString(R.string.share_moment), nickName);
            }

            if (stageDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
                imageUrl = stageDto.getVideoData().getCoverUrl();
            } else if (stageDto.getMediaType().equals(MediaTypeEnum.IMAGE.getCode())) {
                if (stageDto.getImageData().getImages() != null && stageDto.getImageData().getImages().size() > 0) {
                    imageUrl = stageDto.getImageData().getImages().get(0).getUrl();
                }
            }

            switch (v.getId()) {
                case R.id.layout_share_we_chat_friend:
                    ShareUtils.shareWeChatFriend(mContext, title, content, shareUrl, imageUrl);
                    break;
                case R.id.layout_share_wechat_circle:
                    ShareUtils.shareWeChatCircle(mContext,title,content,shareUrl,imageUrl);
                    break;
                case R.id.layout_share_facebook:
                    ShareUtils.shareFacebook(mContext, title, content, shareUrl, imageUrl);
                    break;
                case R.id.layout_share_weibo:
                    ShareUtils.shareWeibo(mContext, title, content, shareUrl, imageUrl);
                    break;
                case R.id.layout_share_qq:
                    ShareUtils.shareQQFriend(mContext, title, content, shareUrl, imageUrl);
                    break;
            }
        }
    }
}
