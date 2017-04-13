package com.knigego.nimo.demoregister.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.crooods.common.domain.view.BizData4Page;
import com.crooods.common.protocol.ResponseT;
import com.crooods.wd.dto.UserDto;
import com.crooods.wd.dto.UserProfileDto;
import com.crooods.wd.dto.post.MomentDto;
import com.crooods.wd.dto.post.MomentItemDto;
import com.crooods.wd.dto.post.StageDto;
import com.crooods.wd.service.IMomentStubService;
import com.crooods.wd.service.IStageStubService;
import com.crooods.wd.service.IUserProfileStubService;
import com.crooods.wd.service.IUserStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.DemoRegisterApplication;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.activities.MainActivity;
import com.knigego.nimo.demoregister.ui.activities.PersonalInfoActivity;
import com.knigego.nimo.demoregister.ui.base.BaseRefreshFragment;
import com.knigego.nimo.demoregister.ui.model.MomentItem;
import com.knigego.nimo.demoregister.ui.model.PersonalInfo;
import com.knigego.nimo.demoregister.ui.model.StageItem;
import com.knigego.nimo.demoregister.ui.model.UserProfileItem;
import com.knigego.nimo.demoregister.ui.typehelper.MomentItemTypeHelper;
import com.knigego.nimo.demoregister.ui.typehelper.PersonalInfoTypeHelper;
import com.knigego.nimo.demoregister.ui.typehelper.StageItemHelper;
import com.knigego.nimo.demoregister.ui.typehelper.UserContentEmptyTypeHelper;
import com.knigego.nimo.demoregister.ui.typehelper.UserInfoTypeHelper;
import com.knigego.nimo.demoregister.ui.typehelper.UserPostTypeItemTypeHelper;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.uimanager.VarietyTypeAdapter;
import com.knigego.nimo.demoregister.util.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 我的
 * Created by ThinkPad on 2017/3/28.
 */

public class UserInfoFragment extends BaseRefreshFragment {

    @Bind(R.id.layout_empty)
    RelativeLayout mEmptyLayout;


    private long userId = 0;
    private boolean isMy = true;//是否首页是我的
    private String userRole;

    private UserInfoTypeHelper mUserInfoType;
    private UserContentEmptyTypeHelper mUserContentEmptyType;
    private StageItemHelper mStageItemHelper;
    private UserPostTypeItemTypeHelper mPostChooseType;
    private MomentItemTypeHelper mMomentItemType;
    private PersonalInfoTypeHelper mPersonalInfoType;

    private int mType = AppConstants.TYPE_STAGE;
    private int mStagePage = 1;

    private int mMomentPage = 1;
    private int mStageCount = 0;

    private int mMomentCount = 0;
    private int mPageCount = 10;

    private ItemViewTypeHelperManager.ItemViewData mChooseTypeData;
    private ItemViewTypeHelperManager.ItemViewData mUserInfoData;
    private ItemViewTypeHelperManager.ItemViewData mEmptyContentData;
    private ItemViewTypeHelperManager.ItemViewData mPersonalInfoData;

    private List<StageItem> mStageItems = new ArrayList<>();
    private List<MomentItem> mMomentItems = new ArrayList<>();

    public static UserInfoFragment newInstance(long userId, String userRole) {
        UserInfoFragment userInfoFragment = new UserInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("userId", userId);
        bundle.putString("userRole", userRole);
        userInfoFragment.setArguments(bundle);
        return userInfoFragment;
    }

    @Override
    protected void _onCreateView(View view) {
        mEmptyLayout.setVisibility(View.GONE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getLong("userId", 0);
            userRole = bundle.getString("userRole");
            isMy = false;
        } else {
            userId = AppPref.getInstance().getUserId();
            userRole = AppPref.getInstance().getUserRole();
        }

        if (DemoRegisterApplication.isRole(userRole)) {//用户角色为大C
            mType = AppConstants.TYPE_STAGE;
        } else {//用户角色为小c时只有动态
            mType = AppConstants.TYPE_MOMENT;
        }
        initGridView(2);
        ((GridLayoutManager) mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter != null && mAdapter.getListData().size() > 0) {
                    ItemViewTypeHelperManager.ItemViewData itemViewData = mAdapter.getListData().get(position);

                    if (itemViewData instanceof MomentItem) {
                        return 1;
                    } else {
                        return ((GridLayoutManager) mLayoutManager).getSpanCount();
                    }
                }
                return ((GridLayoutManager) mLayoutManager).getSpanCount();
            }
        });

        mAdapter = new VarietyTypeAdapter(getItemManager(), mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MomentSpaceItemDecoration(getActivity()));
        //如果是id是我的
        if (isMy) {//获取自己的信息，舞台信息，动态信息
            loadUserInfo();
            List<StageDto> stageDtos = AppPref.getInstance().getMyStages();
            if (stageDtos != null) {
                updateStages(stageDtos);
            }
            List<MomentItemDto> momentItemDtos = AppPref.getInstance().getMyMoments();
            if (momentItemDtos != null) {
                updateMoments(momentItemDtos);
            }
        } else {
            loadPersonalInfo();
        }

        mChooseTypeData = new ItemViewTypeHelperManager.ItemViewData();
        mChooseTypeData.setItemViewTypeHelper(mPostChooseType);

        if (userId == 0) {
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                loadFirstData();
            }
        }, 300);
    }

    private ItemViewTypeHelperManager getItemManager() {
        if (mItemViewTypeHelperManager == null) {
            mItemViewTypeHelperManager = new ItemViewTypeHelperManager();

            mUserInfoType = new UserInfoTypeHelper(getActivity(), R.layout.view_user_info_item);
            mItemViewTypeHelperManager.addType(mUserInfoType);

            mPersonalInfoType = new PersonalInfoTypeHelper(getActivity(), R.layout.view_personal_item);
            mItemViewTypeHelperManager.addType(mPersonalInfoType);

            mUserContentEmptyType = new UserContentEmptyTypeHelper(getActivity(), R.layout.view_user_content_empty, isMy, mType);
            mItemViewTypeHelperManager.addType(mUserContentEmptyType);

            mPostChooseType = new UserPostTypeItemTypeHelper(getActivity(), R.layout.view_user_type_choose, this, isMy);
            mItemViewTypeHelperManager.addType(mPostChooseType);

            mStageItemHelper = new StageItemHelper(getActivity(), R.layout.view_stage_item, true);
            mItemViewTypeHelperManager.addType(mStageItemHelper);

            mMomentItemType = new MomentItemTypeHelper(getActivity(), R.layout.view_moment_item);
            mItemViewTypeHelperManager.addType(mMomentItemType);
        }
        return mItemViewTypeHelperManager;
    }
    @Override
    public void loadMore() {
        super.loadMore();
        if (mType == AppConstants.TYPE_MOMENT) {
            loadMoments();
        } else if (mType == AppConstants.TYPE_STAGE) {
            loadStages();
        }
    }

    private void loadFirstData() {//获取舞台和动态
        if (mStageItems.size() == 0 && mMomentItems.size() == 0) {
            mStagePage = 1;
            loadStages();
            mMomentPage = 1;
            loadMoments();
        } else {
            if (mType == AppConstants.TYPE_STAGE) {
                mStagePage = 1;
                loadStages();
            } else if (mType == AppConstants.TYPE_MOMENT) {
                mMomentPage = 1;
                loadMoments();
            }
        }
    }

    private void loadMoments() {
        IMomentStubService stubService = createApi(IMomentStubService.class);
        stubService.moments(AppPref.getInstance().getAccessToken(), "", userId, 0, mPageCount, mMomentPage,
                new RetrofitUtil.ActivityCallback<ResponseT<BizData4Page<MomentItemDto>>>(getActivity()) {
                    @Override
                    public void success(ResponseT<BizData4Page<MomentItemDto>> bizData4PageResponseT, Response response) {
                        refreshComplete();
                        super.success(bizData4PageResponseT, response);
                        if (bizData4PageResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            BizData4Page<MomentItemDto> bizData4Page = bizData4PageResponseT.getBizData();
                            mMomentCount = bizData4Page.getRecords();
                            totalCount = mMomentCount;
                            List<MomentItemDto> momentItemDtos = bizData4Page.getRows();
                            if (mMomentPage == 1) {
                                if (isMy) {
                                    AppPref.getInstance().saveMyMoments(momentItemDtos);
                                }

                            }
                            updateMoments(momentItemDtos);
                        }
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        refreshComplete();
                        super.failure(error);
                    }
                });
    }

    private void loadStages() {
        IStageStubService stubService = createApi(IStageStubService.class);
        stubService.stages(AppPref.getInstance().getAccessToken(), "", userId, 0, mPageCount, mStagePage,
                new RetrofitUtil.ActivityCallback<ResponseT<BizData4Page<StageDto>>>(getActivity()) {
                    @Override
                    public void success(ResponseT<BizData4Page<StageDto>> bizData4PageResponseT, Response response) {
                        refreshComplete();
                        super.success(bizData4PageResponseT, response);
                        if (bizData4PageResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            BizData4Page<StageDto> data4PageResponseT = bizData4PageResponseT.getBizData();
                            mStageCount = data4PageResponseT.getRecords();
                            totalCount = mStageCount;
                            List<StageDto> stageDtos = data4PageResponseT.getRows();
                            if (mStagePage == 1) {
                                if (isMy) {
                                    AppPref.getInstance().saveMyStages(stageDtos);
                                }
                            }
                            updateStages(stageDtos);
                        }
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        refreshComplete();
                        super.failure(error);
                    }
                });
    }

    private void loadPersonalInfo() {
        IUserStubService stubService = createApi(IUserStubService.class);
        stubService.getUserInfo(AppPref.getInstance().getAccessToken(), userId,
                new RetrofitUtil.ActivityCallback<ResponseT<UserDto>>(getActivity()) {
                    @Override
                    public void success(ResponseT<UserDto> userDtoResponseT, Response response) {
                        super.success(userDtoResponseT, response);
                        if (userDtoResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            UserDto userDto = userDtoResponseT.getBizData();
                            updatePersonalInfo(userDto);
                        }
                    }
                });
    }

    private void updatePersonalInfo(UserDto userDto) {
        if (userDto != null && userDto.getUserBaseDto() != null
                && !TextUtils.isEmpty(userDto.getUserBaseDto().getNickName())) {
            if (getActivity() instanceof PersonalInfoActivity) {
                ((PersonalInfoActivity) getActivity()).updateUserTitle(userDto.getUserBaseDto().getNickName());
            }
            PersonalInfo personalInfo = new PersonalInfo();
            personalInfo.setUserDto(userDto);
            personalInfo.setItemViewTypeHelper(mPersonalInfoType);
            mPersonalInfoData = personalInfo;

            mEmptyContentData = new ItemViewTypeHelperManager.ItemViewData();
            mEmptyContentData.mItemViewTypeHelper = mUserContentEmptyType;

            updateUI();
        }
    }

    private void updateMoments(List<MomentItemDto> momentItemDtos) {
        if (momentItemDtos == null) {
            return;
        }
        List<MomentItem> momentItems = new ArrayList<>();
        for (int i = 0; i < momentItemDtos.size(); i++) {
            MomentItemDto momentItemDto = momentItemDtos.get(i);
            if (momentItemDto.getType().equals("moment")) {
                MomentItem momentItem = new MomentItem();
                momentItem.setMomentDto(momentItemDto.getMoment());
                momentItem.setItemViewTypeHelper(mMomentItemType);
                momentItems.add(momentItem);
            }
        }
        if (mMomentPage == 1) {
            mMomentItems.clear();
        }
        mMomentItems.addAll(momentItems);
        mMomentPage++;
        updateUI();
    }

    private void updateStages(List<StageDto> stageDtos) {
        if (stageDtos == null) {
            return;
        }
        List<StageItem> stageItems = new ArrayList<>();
        for (int i = 0; i < stageDtos.size(); i++) {
            stageItems.add(new StageItem(stageDtos.get(i), mStageItemHelper));
        }
        if (mStagePage == 1) {
            mStageItems.clear();
        } else {
        }
        mStageItems.addAll(stageItems);
        mStagePage++;
        updateUI();
    }

    private void loadUserInfo() {
        IUserProfileStubService stubService = createApi(IUserProfileStubService.class);
        stubService.getUserProfile(AppPref.getInstance().getAccessToken(),
                new RetrofitUtil.ActivityCallback<ResponseT<UserProfileDto>>(getActivity()) {
                    @Override
                    public void success(ResponseT<UserProfileDto> userProfileDtoResponseT, Response response) {
                        super.success(userProfileDtoResponseT, response);
                        if (userProfileDtoResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            UserProfileDto userProfileDto = userProfileDtoResponseT.getBizData();
                            if (userId == 0) {
                                updateUserInfo(userProfileDto);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRefreshLayout.setRefreshing(true);
                                        loadFirstData();
                                    }
                                }, 300);
                            } else {
                                updateUserInfo(userProfileDto);
                            }
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMy) {
            UserProfileDto userProfileDto = AppPref.getInstance().getUserProfile();
            updateUserInfo(userProfileDto);
        }

    }

    private void updateUserInfo(UserProfileDto userProfileDto) {
        if (userProfileDto != null && userProfileDto.getUserBaseDto() != null &&
                !TextUtils.isEmpty(userProfileDto.getUserBaseDto().getNickName())) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateUserTitle(userProfileDto.getUserBaseDto().getNickName());
            }
        }
        AppPref.getInstance().saveUserProfile(userProfileDto);
        UserProfileItem userProfileItem = new UserProfileItem(userProfileDto, mUserInfoType);
        mUserInfoData = userProfileItem;

        mEmptyContentData = new ItemViewTypeHelperManager.ItemViewData();
        mEmptyContentData.mItemViewTypeHelper = mUserContentEmptyType;
        if (userId == 0 || TextUtils.isEmpty(userRole)) {
            userId = AppPref.getInstance().getUserId();
            userRole = AppPref.getInstance().getUserRole();
            if (DemoRegisterApplication.isRole(userRole)) {
                mType = AppConstants.TYPE_STAGE;
            } else {
                mType = AppConstants.TYPE_MOMENT;
            }
        }

        updateUI();
    }

    private void updateUI() {
        mDatas.clear();
        if (isMy) {
            if (mUserInfoData != null) {
                mDatas.add(mUserInfoData);
            }
        } else {
            if (mPersonalInfoData != null) {
                mDatas.add(mPersonalInfoData);
            }
        }

        if (mStageItems.size() > 0 || mMomentItems.size() > 0) {
            Log.i("----------", "updateUI: " + mStageItems.size());
            if (DemoRegisterApplication.isRole(userRole)) {
                mDatas.add(mChooseTypeData);
            }

            if (mType == AppConstants.TYPE_STAGE) {
                mDatas.addAll(mStageItems);
            } else if (mType == AppConstants.TYPE_MOMENT) {
                mDatas.addAll(mMomentItems);
            }
        } else {
            if (mEmptyContentData != null) {
                mDatas.add(mEmptyContentData);
            }
        }
        mAdapter.notifyDataSetChanged();
    }



    @Override
    public void onRefresh() {
        if (isMy) {
            loadUserInfo();
        } else {
            loadPersonalInfo();
        }
        loadFirstData();
    }

    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setTypeChoose(int type) {
        mType = type;
        if (mType == AppConstants.TYPE_STAGE) {
            totalCount = mStageCount;
            if (mStageItems.size() == 0) {
                loadFirstData();
            } else {
                updateUI();
            }
        } else if (mType == AppConstants.TYPE_MOMENT) {
            totalCount = mMomentCount;
            if (mMomentItems.size() == 0) {
                loadFirstData();
            } else {
                updateUI();
            }
            updateUI();
        }
    }
    //分割线
    private class MomentSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private Context mContext;
        private int mTopTypeCount;

        public MomentSpaceItemDecoration(Context context) {
            mContext = context;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            mTopTypeCount = DemoRegisterApplication.isRole(userRole) ? 2 : 1;
            int margin = DeviceUtils.dipToPX(mContext, 4);
            int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
            int position = parent.getChildAdapterPosition(view);

            if (mAdapter != null && mAdapter.getListData().size() > 0) {
                ItemViewTypeHelperManager.ItemViewData itemViewData = mAdapter.getListData().get(position);
                if (itemViewData instanceof MomentItem) {
                    if ((position + mTopTypeCount + 1) % spanCount == 0) {
                        outRect.right = margin;
                    } else {
                        outRect.left = margin;
                        outRect.right = margin;
                    }
                    outRect.bottom = margin;
                } else {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.bottom = 0;
                }
            }
        }
    }
    //广播更新
    @Override
    protected void onUpdateReceiver(Intent intent) {
        super.onUpdateReceiver(intent);
        String action = intent.getAction();
        if (action.equals(AppConstants.ACTION_STAGE_DELETE)) {
            long id = intent.getLongExtra("id", 0);
            if (mAdapter == null) {
                return;
            }
            for (int i = 0; i < mStageItems.size(); i++) {
                StageItem stageItem = mStageItems.get(i);
                if (stageItem.getStageDto().getId() == id) {
                    mStageItems.remove(i);
                    mAdapter.notifyItemRemoved(i);
                    mAdapter.notifyDataSetChanged();
                    updateUI();
                    return;
                }
            }
        } else if (action.equals(AppConstants.ACTION_MOMENT_DELETE)) {
            long id = intent.getLongExtra("id", 0);
            if (mAdapter == null) {
                return;
            }
            for (int i = 0; i < mMomentItems.size(); i++) {
                MomentItem momentItem = mMomentItems.get(i);
                MomentDto momentDto = momentItem.getMomentDto();
                if (momentDto.getId() == id) {
                    mMomentItems.remove(i);
                    mAdapter.notifyItemRemoved(i);
                    mAdapter.notifyDataSetChanged();
                    updateUI();
                    return;
                }
            }
        } else if (action.equals(AppConstants.ACTION_USER_INFO_UPDATE)) {
            if (isMy) {
                loadUserInfo();
            }

        }
    }
}
