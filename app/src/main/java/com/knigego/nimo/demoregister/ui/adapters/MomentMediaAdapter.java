package com.knigego.nimo.demoregister.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.activities.ReleaseMomentActivity;
import com.knigego.nimo.demoregister.ui.model.MomentMediaItem;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * Created by ThinkPad on 2017/4/8.
 */

public class MomentMediaAdapter extends RecyclerView.Adapter {
    private static final int IMAGE_COUNT = 9;
    private static final MomentMediaItem ADD_MEDIA = new MomentMediaItem(true);
    private Context mContext;
    private List<MomentMediaItem> mdatas = new ArrayList<>();


    public MomentMediaAdapter(Context context, ArrayList<MomentMediaItem> momentMediaItems) {
        this.mContext = context;
        this.mdatas = momentMediaItems;
        initData();
    }

    private void initData() {
        if (mdatas.size() < IMAGE_COUNT) {
            mdatas.add(ADD_MEDIA);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_moment_media_add, parent, false);
        return new MomentMediaHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MomentMediaHolder mediaHolder = (MomentMediaHolder) holder;
        final MomentMediaItem mediaItem = mdatas.get(position);
        if (mediaItem.isAddView()) {
            mediaHolder.mImageAdd.setVisibility(View.VISIBLE);
            mediaHolder.mLayoutImage.setVisibility(View.GONE);
        } else {
            mediaHolder.mImageAdd.setVisibility(View.GONE);
            mediaHolder.mLayoutImage.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(Uri.parse("file://" + mediaItem.getFilePath()))//文件路径
                    .placeholder(R.color.image_default_color)
                    .error(R.drawable.error_image_one_third_w)
                    .centerCrop()
                    .into(mediaHolder.mImage);
            if (mediaItem.getMediaType() == MomentMediaItem.MEDIA_TYPE_VIDEO) {
                mediaHolder.mImagePlay.setVisibility(View.VISIBLE);
            } else {
                mediaHolder.mImagePlay.setVisibility(View.GONE);
            }
        }

        mediaHolder.mImageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMomentMediaCount() > 0 && !hasVideo()) {
                    ((ReleaseMomentActivity) mContext).openImageAlbum();
                } else {
                    showChooseMediaDialog();
                }
            }
        });

        mediaHolder.mLayoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaItem.getMediaType() == MomentMediaItem.MEDIA_TYPE_IMAGE) {
                    ((ReleaseMomentActivity) mContext).openPictureView(position);
                } else {
                    ((ReleaseMomentActivity) mContext).openVideoView();
                }
            }
        });

    }

    //是否包含视频
    private boolean hasVideo() {
        for (int i = 0; i < mdatas.size(); i++) {
            if (mdatas.get(i).getMediaType() == MomentMediaItem.MEDIA_TYPE_VIDEO) {
                return true;
            }
        }
        return false;
    }

    //获取个数
    private int getMomentMediaCount() {
        int count = getItemCount();
        if (mdatas.indexOf(ADD_MEDIA) != -1) {
            return count - 1;
        } else {

            return count;
        }
    }

    private void showChooseMediaDialog() {
        DialogPlus dialogPlus = DialogPlus.newDialog(mContext)
                .setContentHolder(new ViewHolder(R.layout.popup_media_choose))
                .setGravity(Gravity.CENTER)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.text_video:
                                ((ReleaseMomentActivity) mContext).openVideoAlbum();
                                break;
                            case R.id.text_image:
                                ((ReleaseMomentActivity) mContext).openImageAlbum();
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .create();
        dialogPlus.show();
    }

    @Override
    public int getItemCount() {
        return mdatas.size();
    }

    public void setData(List<MomentMediaItem> mediaItems) {
        if (mediaItems.indexOf(ADD_MEDIA) != -1) {
            mediaItems.remove(ADD_MEDIA);
        }
        mdatas= mediaItems;
        initData();
        notifyDataSetChanged();

    }

    public List<MomentMediaItem> getMediaItems() {
        return null;
    }

    public void setItemUploadComplete(String key, String imagePath) {
        for (int i = 0; i < mdatas.size(); i++) {
            MomentMediaItem momentMediaItem = mdatas.get(i);
            if (momentMediaItem.getMediaType() == MomentMediaItem.MEDIA_TYPE_IMAGE) {
                if (momentMediaItem.getKey().equals(key)) {
                    momentMediaItem.setUpLoading(false);
                    momentMediaItem.setImageUrl(imagePath);
                    return;
                }
            }
        }

    }

    public boolean isAllImageUploadComplete() {
        for (int i = 0; i < mdatas.size(); i++) {
            MomentMediaItem mediaItem = mdatas.get(i);
            if (mediaItem.getMediaType() == MomentMediaItem.MEDIA_TYPE_IMAGE) {
                if (mediaItem.isUpLoading()) {
                    return false;
                }
            }

        }
        return true;
    }

    public void addMediaItem(MomentMediaItem momentMediaItem) {
        if (mdatas.indexOf(ADD_MEDIA) != -1) {
            mdatas.remove(ADD_MEDIA);
        }
        mdatas.add(momentMediaItem);
        if (momentMediaItem.getMediaType() == MomentMediaItem.MEDIA_TYPE_IMAGE) {//如果是图片，则将addView添加进去
            initData();
        }
        notifyDataSetChanged();
    }

    static class MomentMediaHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image)
        ImageView mImage;
        @Bind(R.id.image_play)
        ImageView mImagePlay;
        @Bind(R.id.layout_image)
        RelativeLayout mLayoutImage;
        @Bind(R.id.image_add)
        ImageView mImageAdd;

        public MomentMediaHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
