package com.knigego.nimo.demoregister.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.knigego.nimo.demoregister.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Folder;

/**
 *
 * Created by ThinkPad on 2017/4/10.
 */

public class FolderAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Folder> mFolders = new ArrayList<>();
    private int mImageSize;
    private int lastSelected = 0;


    public FolderAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageSize = mContext.getResources().getDimensionPixelOffset(me.nereo.multi_image_selector.R.dimen.mis_folder_cover_size);
    }

    @Override
    public int getCount() {
        return mFolders.size() + 1;
    }

    @Override
    public Folder getItem(int position) {
        if (position == 0) {
            return null;
        }
        return mFolders.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(me.nereo.multi_image_selector.R.layout.mis_list_item_folder, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder != null) {
            if (position == 0) {

                holder.name.setText(me.nereo.multi_image_selector.R.string.mis_folder_all);
                holder.path.setText("/sdcard");
                holder.size.setText(String.format("%d%s",
                        getTotalImageSize(), mContext.getResources().getString(R.string.mis_photo_unit)));
                if (mFolders.size() > 0) {
                    Folder folder = mFolders.get(0);
                    Glide.with(mContext)
                            .load(new File(folder.cover.path))
                            .placeholder(R.color.image_default_color)
                            .error(R.drawable.error_image_one_third_w)
                            .centerCrop().into(holder.cover);
                }
            } else {
                holder.bindData(getItem(position));
            }
            if (lastSelected == position) {
                holder.indicator.setVisibility(View.VISIBLE);
            } else {
                holder.indicator.setVisibility(View.INVISIBLE);
            }
        }
            return convertView;
        }


    private int getTotalImageSize() {
        int result = 0;
        if (mFolders != null && mFolders.size() > 0) {
            for (Folder f : mFolders) {
                result += f.images.size();
            }
        }
        return result;
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    public void setSelectIndex(int position) {
        if (lastSelected == position) {
            return;
        }

        lastSelected = position;
        notifyDataSetChanged();
    }

    //设置数据集
    public void setData(ArrayList<Folder> resultFolder) {
        if (resultFolder != null && resultFolder.size() > 0) {
            mFolders = resultFolder;
        } else {
            mFolders.clear();
        }

        notifyDataSetChanged();

    }

    class ViewHolder {
        ImageView cover;
        TextView name;
        TextView path;
        TextView size;
        ImageView indicator;

        ViewHolder(View view) {
            cover = (ImageView) view.findViewById(me.nereo.multi_image_selector.R.id.cover);
            name = (TextView) view.findViewById(me.nereo.multi_image_selector.R.id.name);
            path = (TextView) view.findViewById(me.nereo.multi_image_selector.R.id.path);
            size = (TextView) view.findViewById(me.nereo.multi_image_selector.R.id.size);
            indicator = (ImageView) view.findViewById(me.nereo.multi_image_selector.R.id.indicator);
            view.setTag(this);
        }

        public void bindData(Folder item) {
            if (item == null) {
                return;
            }
            name.setText(item.name);
            path.setText(item.path);
            if (item.images != null) {
                size.setText(String.format("%d%s", item.images.size(), mContext.getResources().getString(R.string.mis_photo_unit)));
            } else {
                size.setText("*" + mContext.getResources().getString(R.string.mis_photo_unit));
            }

            //显示图片
            if (item.cover != null) {
                Glide.with(mContext)
                        .load(new File(item.cover.path))
                        .placeholder(R.color.image_default_color)
                        .error(R.drawable.error_image_one_third_w)
                        .centerCrop().into(cover);
            } else {
                cover.setImageResource(R.drawable.error_image_one_third_w);
            }

        }
    }
}
