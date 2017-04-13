package com.knigego.nimo.demoregister.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.crooods.wd.dto.post.MediaImageDto;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.adapters.SuperStarAdapter;
import com.knigego.nimo.demoregister.util.CommonUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页发送图片展示列表
 * Created by ThinkPad on 2017/3/29.
 */

public class ImageViewWrapper extends LinearLayout {

    @Bind(R.id.image_one)
    ImageView mImageOne;
    @Bind(R.id.image_two_1)
    ImageView mImageTwo1;
    @Bind(R.id.image_two_2)
    ImageView mImageTwo2;
    @Bind(R.id.layout_two)
    LinearLayout mLayoutTwo;
    @Bind(R.id.image_three_1)
    ImageView mImageThree1;
    @Bind(R.id.image_three_2)
    ImageView mImageThree2;
    @Bind(R.id.image_three_3)
    ImageView mImageThree3;
    @Bind(R.id.layout_three)
    LinearLayout mLayoutThree;
    @Bind(R.id.image_four_1)
    ImageView mImageFour1;
    @Bind(R.id.image_four_2)
    ImageView mImageFour2;
    @Bind(R.id.image_four_3)
    ImageView mImageFour3;
    @Bind(R.id.image_four_4)
    ImageView mImageFour4;
    @Bind(R.id.layout_four)
    LinearLayout mLayoutFour;
    @Bind(R.id.image_five_six_1)
    ImageView mImageFiveSix1;
    @Bind(R.id.image_five_six_2)
    ImageView mImageFiveSix2;
    @Bind(R.id.image_five_six_3)
    ImageView mImageFiveSix3;
    @Bind(R.id.image_five_six_4)
    ImageView mImageFiveSix4;
    @Bind(R.id.image_five_six_5)
    ImageView mImageFiveSix5;
    @Bind(R.id.image_five_six_6)
    ImageView mImageFiveSix6;
    @Bind(R.id.layout_five_six)
    LinearLayout mLayoutFiveSix;
    @Bind(R.id.image_seven_eight_nine_1)
    ImageView mImageSevenEightNine1;
    @Bind(R.id.image_seven_eight_nine_2)
    ImageView mImageSevenEightNine2;
    @Bind(R.id.image_seven_eight_nine_3)
    ImageView mImageSevenEightNine3;
    @Bind(R.id.image_seven_eight_nine_4)
    ImageView mImageSevenEightNine4;
    @Bind(R.id.image_seven_eight_nine_5)
    ImageView mImageSevenEightNine5;
    @Bind(R.id.image_seven_eight_nine_6)
    ImageView mImageSevenEightNine6;
    @Bind(R.id.image_seven_eight_nine_7)
    ImageView mImageSevenEightNine7;
    @Bind(R.id.image_seven_eight_nine_8)
    ImageView mImageSevenEightNine8;
    @Bind(R.id.image_seven_eight_nine_9)
    ImageView mImageSevenEightNine9;
    @Bind(R.id.layout_seven_eight_nine)
    LinearLayout mLayoutSevenEightNine;

    public ImageViewWrapper(Context context) {
        super(context);
        init(context);
    }

    public ImageViewWrapper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_image_wrapper, this);
        ButterKnife.bind(this, view);
    }

    public void setImages(List<MediaImageDto> images) {

        //获取图片，然后设置
        if (images != null && images.size() > 0) {
            int count = images.size();
            String[] pictures = CommonUtils.getPictures(images);
            switch (count) {
                case 1:
                    Glide.with(getContext()).load(images.get(0).getUrl())
                            .asBitmap().into(mImageOne);
                    mImageOne.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 0, getContext()));
                    mImageOne.setVisibility(VISIBLE);
                    mLayoutTwo.setVisibility(GONE);
                    mLayoutThree.setVisibility(GONE);
                    mLayoutFour.setVisibility(GONE);
                    mLayoutFiveSix.setVisibility(GONE);
                    mLayoutSevenEightNine.setVisibility(GONE);
                    break;
                case 2:
                    loadImage(mImageTwo1, images.get(0).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageTwo2, images.get(1).getUrl(), R.drawable.error_image_a_half_w);
                    mImageTwo1.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 0, getContext()));
                    mImageTwo2.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 1, getContext()));
                    mImageOne.setVisibility(GONE);
                    mLayoutTwo.setVisibility(VISIBLE);
                    mLayoutThree.setVisibility(GONE);
                    mLayoutFour.setVisibility(GONE);
                    mLayoutFiveSix.setVisibility(GONE);
                    mLayoutSevenEightNine.setVisibility(GONE);
                    break;
                case 3:
                    loadImage(mImageThree1, images.get(0).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageThree2, images.get(1).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageThree3, images.get(2).getUrl(), R.drawable.error_image_a_half_w);
                    mImageThree1.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 0, getContext()));
                    mImageThree2.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 1, getContext()));
                    mImageThree3.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 2, getContext()));
                    mImageOne.setVisibility(GONE);
                    mLayoutTwo.setVisibility(GONE);
                    mLayoutFour.setVisibility(GONE);
                    mLayoutThree.setVisibility(VISIBLE);
                    mLayoutFiveSix.setVisibility(GONE);
                    mLayoutSevenEightNine.setVisibility(GONE);
                    break;
                case 4:
                    loadImage(mImageFour1, images.get(0).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageFour2, images.get(1).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageFour3, images.get(2).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageFour4, images.get(3).getUrl(), R.drawable.error_image_a_half_w);
                    mImageFour1.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 0, getContext()));
                    mImageFour2.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 1, getContext()));
                    mImageFour3.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 2, getContext()));
                    mImageFour4.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 3, getContext()));
                    mImageOne.setVisibility(GONE);
                    mLayoutTwo.setVisibility(GONE);
                    mLayoutThree.setVisibility(GONE);
                    mLayoutFiveSix.setVisibility(GONE);
                    mLayoutFiveSix.setVisibility(VISIBLE);
                    mLayoutSevenEightNine.setVisibility(GONE);
                    break;
                case 5:
                    loadImage(mImageFiveSix1, images.get(0).getUrl(), R.drawable.error_image_one_third_w);
                    loadImage(mImageFiveSix2, images.get(1).getUrl(), R.drawable.error_image_one_third_w);
                    loadImage(mImageFiveSix3, images.get(2).getUrl(), R.drawable.error_image_one_third_w);
                    loadImage(mImageFiveSix4, images.get(3).getUrl(), R.drawable.error_image_one_third_w);
                    loadImage(mImageFiveSix5, images.get(4).getUrl(), R.drawable.error_image_one_third_w);
                    mImageFiveSix1.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 0, getContext()));
                    mImageFiveSix2.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 1, getContext()));
                    mImageFiveSix3.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 2, getContext()));
                    mImageFiveSix4.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 3, getContext()));
                    mImageFiveSix5.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 4, getContext()));
                    mImageFiveSix6.setVisibility(GONE);
                    mImageOne.setVisibility(GONE);
                    mLayoutTwo.setVisibility(GONE);
                    mLayoutThree.setVisibility(GONE);
                    mLayoutFour.setVisibility(GONE);
                    mLayoutFiveSix.setVisibility(VISIBLE);
                    mLayoutSevenEightNine.setVisibility(GONE);
                    break;
                case 6:
                    loadImage(mImageFiveSix1, images.get(0).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageFiveSix2, images.get(1).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageFiveSix3, images.get(2).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageFiveSix4, images.get(3).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageFiveSix5, images.get(4).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageFiveSix6, images.get(5).getUrl(), R.drawable.error_image_a_half_w);
                    mImageFiveSix1.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 0, getContext()));
                    mImageFiveSix2.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 1, getContext()));
                    mImageFiveSix3.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 2, getContext()));
                    mImageFiveSix4.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 3, getContext()));
                    mImageFiveSix5.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 4, getContext()));
                    mImageFiveSix6.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 5, getContext()));
                    mImageOne.setVisibility(GONE);
                    mLayoutTwo.setVisibility(GONE);
                    mLayoutThree.setVisibility(GONE);
                    mLayoutFiveSix.setVisibility(VISIBLE);

                    mLayoutFour.setVisibility(GONE);
                    mLayoutSevenEightNine.setVisibility(GONE);
                    break;
                case 7:
                    loadImage(mImageSevenEightNine1, images.get(0).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine2, images.get(1).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine3, images.get(2).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine4, images.get(3).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine5, images.get(4).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine6, images.get(5).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine7, images.get(6).getUrl(), R.drawable.error_image_a_half_w);
                    mImageSevenEightNine1.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 0, getContext()));
                    mImageSevenEightNine2.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 1, getContext()));
                    mImageSevenEightNine3.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 2, getContext()));
                    mImageSevenEightNine4.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 3, getContext()));
                    mImageSevenEightNine5.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 4, getContext()));
                    mImageSevenEightNine6.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 5, getContext()));
                    mImageSevenEightNine7.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 6, getContext()));
                    mImageOne.setVisibility(GONE);
                    mLayoutTwo.setVisibility(GONE);
                    mLayoutThree.setVisibility(GONE);
                    mLayoutFour.setVisibility(GONE);
                    mLayoutFiveSix.setVisibility(GONE);
                    mLayoutSevenEightNine.setVisibility(VISIBLE);
                    mImageSevenEightNine8.setVisibility(GONE);
                    mImageSevenEightNine9.setVisibility(GONE);
                    break;
                case 8:
                    loadImage(mImageSevenEightNine1, images.get(0).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine2, images.get(1).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine3, images.get(2).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine4, images.get(3).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine5, images.get(4).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine6, images.get(5).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine7, images.get(6).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine8, images.get(7).getUrl(), R.drawable.error_image_a_half_w);

                    mImageSevenEightNine1.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 0, getContext()));
                    mImageSevenEightNine2.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 1, getContext()));
                    mImageSevenEightNine3.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 2, getContext()));
                    mImageSevenEightNine4.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 3, getContext()));
                    mImageSevenEightNine5.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 4, getContext()));
                    mImageSevenEightNine6.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 5, getContext()));
                    mImageSevenEightNine7.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 6, getContext()));
                    mImageSevenEightNine8.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 7, getContext()));
                    mImageOne.setVisibility(GONE);
                    mLayoutTwo.setVisibility(GONE);
                    mLayoutThree.setVisibility(GONE);
                    mLayoutFour.setVisibility(GONE);
                    mLayoutSevenEightNine.setVisibility(VISIBLE);

                    mLayoutFiveSix.setVisibility(GONE);
                    mImageSevenEightNine9.setVisibility(GONE);
                    break;
                case 9:
                    loadImage(mImageSevenEightNine1, images.get(0).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine2, images.get(1).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine3, images.get(2).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine4, images.get(3).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine5, images.get(4).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine6, images.get(5).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine7, images.get(6).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine8, images.get(7).getUrl(), R.drawable.error_image_a_half_w);
                    loadImage(mImageSevenEightNine9, images.get(8).getUrl(), R.drawable.error_image_a_half_w);

                    mImageSevenEightNine1.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 0, getContext()));
                    mImageSevenEightNine2.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 1, getContext()));
                    mImageSevenEightNine3.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 2, getContext()));
                    mImageSevenEightNine4.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 3, getContext()));
                    mImageSevenEightNine5.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 4, getContext()));
                    mImageSevenEightNine6.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 5, getContext()));
                    mImageSevenEightNine7.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 6, getContext()));
                    mImageSevenEightNine8.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 7, getContext()));
                    mImageSevenEightNine9.setOnClickListener(new SuperStarAdapter.GotoPictureViewListener(pictures, 8, getContext()));
                    mImageOne.setVisibility(GONE);
                    mLayoutTwo.setVisibility(GONE);
                    mLayoutThree.setVisibility(GONE);
                    mLayoutFour.setVisibility(GONE);
                    mLayoutFiveSix.setVisibility(GONE);
                    mLayoutSevenEightNine.setVisibility(VISIBLE);

                default:
                    break;
            }
        } else {
            mImageOne.setVisibility(GONE);
            mLayoutTwo.setVisibility(GONE);
            mLayoutThree.setVisibility(GONE);
            mLayoutFour.setVisibility(GONE);
            mLayoutFiveSix.setVisibility(GONE);
            mLayoutSevenEightNine.setVisibility(GONE);
        }
    }

    private void loadImage(ImageView imageView, String url, int resId) {
        Glide.with(getContext()).load(url).centerCrop().error(resId).placeholder(R.color.image_default_color)
                .into(imageView);
    }
}
