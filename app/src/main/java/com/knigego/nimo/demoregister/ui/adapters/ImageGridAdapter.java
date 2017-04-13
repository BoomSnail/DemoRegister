package com.knigego.nimo.demoregister.ui.adapters;

import android.content.Context;
import android.graphics.Point;
import android.nfc.cardemulation.OffHostApduService;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by ThinkPad on 2017/4/10.
 */

public class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;
    private Context mContext;
    private LayoutInflater mInflater;


    private boolean mIsShowCamera = true;
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    int mGridWith;

    public ImageGridAdapter(Context context, boolean isshowCamera, int count) {

        mContext = context;
        this.mIsShowCamera = isshowCamera;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            windowManager.getDefaultDisplay().getSize(size);
            width = size.x;
        } else {
            width = windowManager.getDefaultDisplay().getWidth();
        }

        mGridWith = width / count;
    }

    @Override
    public int getCount() {
        return mIsShowCamera ? mImages.size() + 1 : mImages.size();
    }

    @Override
    public Image getItem(int position) {
        if (mIsShowCamera) {
            if (position == 0) {
                return null;
            }
            return mImages.get(position - 1);
        } else {
            return mImages.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isShowCamera()) {
            if (position == 0) {
                convertView = mInflater.inflate(me.nereo.multi_image_selector.R.layout.mis_list_item_camera, parent, false);
                return convertView;
            }
        }
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(me.nereo.multi_image_selector.R.layout.mis_list_item_image, parent, false);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (viewHolder != null) {
            viewHolder.bindData(getItem(position));
        }

        return convertView;
    }

    /**
     * 显示选择指示器
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;

    }

    public boolean isShowCamera() {
        return mIsShowCamera;
    }

    public void setShowCamera(boolean b) {
        if (mIsShowCamera == b) {
            return;
        }
        mIsShowCamera = b;
        notifyDataSetChanged();
    }

    public void setData(List<Image> images) {

        mSelectedImages.clear();
        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (mIsShowCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    /**
     * 通过图片路径设置默认选择
     *
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for (String path : resultList) {
            Image image = getImageByPath(path);
            if (image != null) {
                mSelectedImages.add(image);
            }
        }
        if (mSelectedImages.size() > 0) {
            notifyDataSetChanged();
        }
    }

    private Image getImageByPath(String path) {
        if (mImages != null && mImages.size() > 0) {
            for (Image image : mImages) {
                if (image.path.equalsIgnoreCase(path)) {
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * 选择某个图片改变状态
     */
    public void select(Image image) {
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
        } else {
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView image;
        //        ImageView indicator;
//        View mask;
        ImageView imageCheck;


        ViewHolder(View view) {
            image = (ImageView) view.findViewById(me.nereo.multi_image_selector.R.id.image);
            imageCheck = (ImageView) view.findViewById(me.nereo.multi_image_selector.R.id.image_check);
//            indicator = (ImageView) view.findViewById(R.id.checkmark);
//            mask = view.findViewById(R.id.mask);
            view.setTag(this);
        }

        public void bindData(Image item) {
            if (item == null) {
                return;
            }

            if (showSelectIndicator) {
                if (mSelectedImages.contains(item)) {
                    if (mSelectedImages.contains(item)) {
                        imageCheck.setVisibility(View.VISIBLE);
                    }
                } else {
                    imageCheck.setVisibility(View.GONE);
                }
            } else {
                imageCheck.setVisibility(View.GONE);
            }

            File imageFile = new File(item.path);
            if (imageFile.exists()) {
                Glide.with(mContext).load(imageFile)
                        .placeholder(R.color.image_default_color)
                        .error(R.drawable.error_image_one_third_w)
                        .centerCrop()
                        .into(image);

            } else {
                image.setImageResource(R.drawable.error_image_one_third_w);

            }
        }
    }
}
