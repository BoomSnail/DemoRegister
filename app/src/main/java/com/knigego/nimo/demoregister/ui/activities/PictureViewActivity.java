package com.knigego.nimo.demoregister.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.util.BitmapUtil;
import com.knigego.nimo.demoregister.util.DeviceUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

public class PictureViewActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    private static final String TAG = PictureViewActivity.class.getSimpleName();
    public static final String PICTURES = "pictures";
    public static final String POSITION = "position";

    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.layout_dots)
    LinearLayout mLayoutDots;

    private String[] images ;
    private int position;

    private boolean isHideDots = false;

    ArrayList<View> mViews ;

    @Override
    public boolean showHomeAsUpEnabled() {
        return true;
    }

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_picture_view);
        ButterKnife.bind(this);
        hideToolbar(false);
        getIntentValue();
        mViews = new ArrayList<>();
        mViewPager.setAdapter(new PictureAdapter(this,images));
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(this);
        initDots(images.length);

        selectPage(position);
    }

    private void selectPage(int position) {
        for (int i = 0; i < mViews.size(); i++) {
            if (i == position) {
                (mViews.get(position)).setBackgroundResource(R.drawable.shape_dot_white);
            } else {
                (mViews.get(i)).setBackgroundResource(R.drawable.shape_dot_gray);
            }

        }
    }

    private void initDots(int length) {
        mLayoutDots.removeAllViews();
        if (length < 0) {
            return;
        }
        int with = DeviceUtils.dipToPX(this,10);
        int margin = DeviceUtils.dipToPX(this,2.5f);
        for (int i = 0; i < length; i++) {
            View dotView = new View(this);
            dotView.setBackgroundResource(R.drawable.shape_dot_gray);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(with,with);
            params.setMargins(margin,0,margin,0);
            mLayoutDots.addView(dotView,params);
            mViews.add(dotView);
        }
    }

    public void getIntentValue() {
        Intent intent = getIntent();
        images = intent.getStringArrayExtra(PICTURES);
        position = intent.getIntExtra(POSITION,0);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class PictureAdapter extends PagerAdapter {

        private Context mContext;

        private String[] pictures;
        public PictureAdapter(Context context,String[] images) {
            mContext = context;
            this.pictures = images;
        }

        public String[] getData(){
            return pictures;
        }
        @Override
        public int getCount() {
            return pictures.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final String imageUrl = pictures[position];
            final PhotoView photoView = new PhotoView(container.getContext());
            Glide.with(mContext).load(imageUrl).into(photoView);


            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DialogPlus dialogPlus = DialogPlus.newDialog(mContext)
                            .setContentHolder(new ViewHolder(R.layout.popmenu_save_picture))
                            .setGravity(Gravity.BOTTOM)
                            .setCancelable(true)
                            .setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(DialogPlus dialog, View view) {
                                    switch (view.getId()) {
                                        case R.id.save:
                                            savePicture(photoView,imageUrl);
                                            dialog.dismiss();
                                            break;
                                        case R.id.cancel:
                                            dialog.dismiss();
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .create();
                    dialogPlus.show();

//                    return false;
                    return false;
                }
            });

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        private void savePicture(PhotoView photoView, String imageUrl) {
            String[] picture = imageUrl.split("/");
            String pictureName = picture[picture.length - 1];
            Bitmap bitmap = ((GlideBitmapDrawable) photoView.getDrawable()).getBitmap();
            String imagePath = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory()
                    .getPath()))).append("/DemoRegister/Image/").append(pictureName).toString();
            Log.i(TAG, "savePicture: " + imagePath);
            boolean saveSuccess = BitmapUtil.bitmapToFile(bitmap, imagePath);
            if (saveSuccess) {
                Toast.makeText(mContext, "图片保存成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "图片已保存或保存失败", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
