package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.dto.StringWrapper;
import com.crooods.wd.dto.UserDto;
import com.crooods.wd.service.IFriendStubService;
import com.crooods.wd.service.IFriendshipsStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.activities.PictureViewActivity;
import com.knigego.nimo.demoregister.ui.model.PersonalInfo;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.util.BrodcastHelper;
import com.knigego.nimo.demoregister.util.GlideCircleTransform;
import com.knigego.nimo.demoregister.util.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 *
 * Created by ThinkPad on 2017/4/2.
 */

public class PersonalInfoTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper {

    public PersonalInfoTypeHelper(Context context, int layout) {
        super(context, layout);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new PersonalViewHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, final int position) {
        PersonalViewHolder personalViewHolder = (PersonalViewHolder) viewHolder;
        final PersonalInfo personalInfo = (PersonalInfo) data;
        final UserDto userDto = personalInfo.getUserDto();
        //头像
        Glide.with(mContext).load(userDto.getUserBaseDto().getAvator()).placeholder(R.drawable.rc_ic_def_msg_portrait)
                .transform(new GlideCircleTransform(mContext)).into(personalViewHolder.mImageAvatar);
        personalViewHolder.mImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(userDto.getUserBaseDto().getAvator())) {

                    Intent intent = new Intent(mContext, PictureViewActivity.class);
                    intent.putExtra(PictureViewActivity.PICTURES, new String[]{userDto.getUserBaseDto().getAvator()});
                    intent.putExtra(PictureViewActivity.POSITION, 0);
                    mContext.startActivity(intent);
                }
            }
        });
        //舞台数量
        personalViewHolder.mTextStageNum.setText(String.valueOf(userDto.getUserCountDto().getStagePosts()));
        //关注的数量
        personalViewHolder.mTextMomentNum.setText(String.valueOf(userDto.getUserCountDto().getMomentPosts()));
        //粉丝数量
        personalViewHolder.mTextFansNum.setText(String.valueOf(userDto.getUserCountDto().getFollowers()));

        //自我说明
        personalViewHolder.mTextBrief.setText(userDto.getUserBaseDto().getIntro());

        //查看他人信息
        personalViewHolder.mBtnCheckUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.goToEditUserInfo(mContext, false, userDto);
            }
        });

        //已是好友，与未时好友的imageView textView
        if (userDto.getFriendsStatus().equals("1")
                || userDto.getFriendsStatus().equals("2")) {
            personalViewHolder.mImageAddFriend.setImageResource(R.drawable.icon_user_follow);
            personalViewHolder.mTextAddFriend.setText(R.string.start_chat);
        } else {
            personalViewHolder.mImageAddFriend.setImageResource(R.drawable.icon_user_add_friend);
            personalViewHolder.mTextAddFriend.setText(R.string.add_friend);
        }

        //关注与未关注
        if (userDto.isFollowed()) {
            personalViewHolder.mImageFollow.setImageResource(R.drawable.icon_user_has_follow);
            personalViewHolder.mTextFollow.setText(R.string.cancel_follow);
        } else {
            personalViewHolder.mImageFollow.setImageResource(R.drawable.icon_user_follow);
            personalViewHolder.mTextFollow.setText(R.string.ta_follow);
        }
        //点击粉丝
        personalViewHolder.mLayoutFans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.gotoMyFans(mContext, userDto.getUserBaseDto().getUserId());
            }
        });

        personalViewHolder.mLayoutAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDto.getFriendsStatus().equals("1")
                        || userDto.getFriendsStatus().equals("2")) {
                    //开始聊天

                } else {
                    addFriendAction(userDto.getUserBaseDto().getUserId());
                }
            }
        });
        personalViewHolder.mLayoutFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDto.getUserBaseDto().getUserId() == AppPref.getInstance().getUserId()) {
//                    Toast.makeText(mContext,"不能管住自己",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (userDto.isFollowed()) {
                    cancelFollow(userDto);
                } else {
                    follow(userDto);
                }
            }
        });


    }

    private void follow(final UserDto userDto) {
        IFriendshipsStubService stubService = RetrofitUtil.createApi(IFriendshipsStubService.class);
        stubService.friendsCreate(AppPref.getInstance().getAccessToken(), userDto.getUserBaseDto().getUserId(),
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            userDto.setFollowed(true);
                            mBaseAdapter.notifyDataSetChanged();

                            //关注后发送广播，更改我的关注
                            BrodcastHelper.sendUserInfoUpdateAction(mContext);
                        }
                    }
                });
    }

    private void cancelFollow(final UserDto userDto) {
        IFriendshipsStubService stubService = RetrofitUtil.createApi(IFriendshipsStubService.class);
        stubService.friendsCancel(AppPref.getInstance().getAccessToken(), userDto.getUserBaseDto().getUserId(),
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            userDto.setFollowed(false);
                            mBaseAdapter.notifyDataSetChanged();

                            BrodcastHelper.sendUserInfoUpdateAction(mContext);
                        }
                    }
                });
    }

    private void addFriendAction(Long userId) {
        if (userId == AppPref.getInstance().getUserId()) {
//            Toast.makeText(mContext,"不能添加自己为好友",Toast.LENGTH_SHORT).show();
            return;
        }

        IFriendStubService iFriendStubService = RetrofitUtil.createApi(IFriendStubService.class);
        iFriendStubService.friendsAddApply(AppPref.getInstance().getAccessToken(), userId,
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(mContext) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
//                            Toast.makeText(mContext,"添加好友申请已发送",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public class PersonalViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image_avatar)
        ImageView mImageAvatar;
        @Bind(R.id.text_stage_num)
        TextView mTextStageNum;
        @Bind(R.id.text_moment_num)
        TextView mTextMomentNum;
        @Bind(R.id.layout_moment)
        LinearLayout mLayoutMoment;
        @Bind(R.id.text_fans_num)
        TextView mTextFansNum;
        @Bind(R.id.layout_fans)
        LinearLayout mLayoutFans;
        @Bind(R.id.layout_stage)
        LinearLayout mLayoutStage;
        @Bind(R.id.btn_check_user_info)
        Button mBtnCheckUserinfo;
        @Bind(R.id.text_brief)
        TextView mTextBrief;
        @Bind(R.id.image_add_friend)
        ImageView mImageAddFriend;
        @Bind(R.id.text_add_friend)
        TextView mTextAddFriend;
        @Bind(R.id.layout_add_friend)
        LinearLayout mLayoutAddFriend;
        @Bind(R.id.image_follow)
        ImageView mImageFollow;
        @Bind(R.id.text_follow)
        TextView mTextFollow;
        @Bind(R.id.layout_follow)
        LinearLayout mLayoutFollow;

        public PersonalViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
