package com.knigego.nimo.demoregister.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.canyinghao.canrefresh.CanRefreshLayout;
import com.crooods.common.domain.view.BizData4Page;
import com.crooods.common.protocol.ResponseT;
import com.crooods.wd.dto.post.MomentItemDto;
import com.crooods.wd.service.IMomentStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.base.BaseFragment;
import com.knigego.nimo.demoregister.ui.model.AdInfo;
import com.knigego.nimo.demoregister.ui.model.MomentItem;
import com.knigego.nimo.demoregister.ui.typehelper.MomentAdTypeHelper;
import com.knigego.nimo.demoregister.ui.typehelper.MomentItemTypeHelper;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.uimanager.VarietyTypeAdapter;
import com.knigego.nimo.demoregister.util.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 动态
 * Created by ThinkPad on 2017/3/28.
 */

public class MomentFragment extends BaseFragment implements CanRefreshLayout.OnRefreshListener, CanRefreshLayout.OnLoadMoreListener {

    @Bind(R.id.layout_empty)
    LinearLayout mLayoutEmpty;
    @Bind(R.id.layout_first_loading)
    LinearLayout mLayoutFirstLoading;
    @Bind(R.id.can_content_view)
    RecyclerView mCanContentView;
    @Bind(R.id.refresh)
    CanRefreshLayout mRefresh;

    private int mPageCount = 10;
    private int mPage = 1;
    private int totalCount = 0;

    private MomentAdTypeHelper mAdTypeHelper;
    private MomentItemTypeHelper mMomentItemTypeHelper;
    private VarietyTypeAdapter mAdapter;
    private ItemViewTypeHelperManager mItemViewTypeHelperManager;
    private List<ItemViewTypeHelperManager.ItemViewData> mItemViewDatas = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_moment;
    }

    //重新加载
    @OnClick(R.id.text_reload)
    public void onViewClicked() {
        mPage = 1;
        loadMoments();
        mLayoutEmpty.setVisibility(View.GONE);
        mLayoutFirstLoading.setVisibility(View.GONE);
    }

    @Override
    protected void onCreateView(View view, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        initUI();

        List<MomentItemDto> momentItemDtos = AppPref.getInstance().getMoments();
        if (momentItemDtos != null) {
            updateMoments(momentItemDtos, false);
        }
        if (momentItemDtos != null && momentItemDtos.size() == 0) {
            mPage = 1;
            loadMoments();
            mLayoutFirstLoading.setVisibility(View.VISIBLE);
        } else {
            mLayoutFirstLoading.setVisibility(View.GONE);
            mRefresh.autoRefresh();
        }
    }

    private void initUI() {
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mCanContentView.setLayoutManager(layoutManager);
        mCanContentView.setItemAnimator(new DefaultItemAnimator());

        mRefresh.setOnLoadMoreListener(this);
        mRefresh.setOnRefreshListener(this);
        mRefresh.setStyle(0, 0);
        mCanContentView.addItemDecoration(new SpaceItemDecoration());
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter != null && mAdapter.getListData().size() > 0) {
                    ItemViewTypeHelperManager.ItemViewData itemViewData = mAdapter.getListData().get(position);
                    if (itemViewData instanceof AdInfo) {
                        return layoutManager.getSpanCount();
                    } else if (itemViewData instanceof MomentItem) {
                        return 1;
                    }
                }
                return 1;
            }
        });
        mAdapter = new VarietyTypeAdapter(getItemTypeManager(), mItemViewDatas);
        mCanContentView.setAdapter(mAdapter);
    }

    private ItemViewTypeHelperManager getItemTypeManager() {
        if (mItemViewTypeHelperManager == null) {
            mItemViewTypeHelperManager = new ItemViewTypeHelperManager();

            mAdTypeHelper = new MomentAdTypeHelper(getActivity(), R.layout.view_moment_ad);
            mItemViewTypeHelperManager.addType(mAdTypeHelper);

            mMomentItemTypeHelper = new MomentItemTypeHelper(getActivity(), R.layout.view_moment_item);
            mItemViewTypeHelperManager.addType(mMomentItemTypeHelper);
        }
        return mItemViewTypeHelperManager;
    }

    private void loadMoments() {
        IMomentStubService stubService = createApi(IMomentStubService.class);
        stubService.moments(AppPref.getInstance().getAccessToken(), "", 0, 0, mPageCount, mPage,
                new RetrofitUtil.ActivityCallback<ResponseT<BizData4Page<MomentItemDto>>>(getActivity()) {
                    @Override
                    public void success(ResponseT<BizData4Page<MomentItemDto>> bizData4PageResponseT, Response response) {
                        super.success(bizData4PageResponseT, response);
                        mRefresh.refreshComplete();
                        mRefresh.loadMoreComplete();
                        mLayoutFirstLoading.setVisibility(View.GONE);
                        if (bizData4PageResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            BizData4Page<MomentItemDto> momentItemDtoBizData4Page = bizData4PageResponseT.getBizData();
                            List<MomentItemDto> momentItemDtos = momentItemDtoBizData4Page.getRows();
                            totalCount = bizData4PageResponseT.getBizData().getRecords();
                            if (mPage == 1) {
                                AppPref.getInstance().saveMoments(momentItemDtos);
                            }
                            updateMoments(momentItemDtos, false);
                            if (mAdapter != null && mItemViewDatas.size() >= totalCount) {
                                mRefresh.setLoadMoreEnabled(false);
                            } else {
                                mRefresh.setLoadMoreEnabled(true);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        mRefresh.refreshComplete();
                        mRefresh.loadMoreComplete();
                        mLayoutFirstLoading.setVisibility(View.GONE);
                    }
                });
    }

    private void updateMoments(List<MomentItemDto> momentItemDtos, boolean isLocation) {
        if (momentItemDtos == null) {
            return;
        }
        if (mPage == 1) {
            mItemViewDatas.clear();
        }

        for (int i = 0; i < momentItemDtos.size(); i++) {
            MomentItemDto momentItemDto = momentItemDtos.get(i);
            String type = momentItemDto.getType();
            if (type.equals("moment")) {
                MomentItem momentItem = new MomentItem();
                momentItem.setMomentDto(momentItemDto.getMoment());
                momentItem.setItemViewTypeHelper(mMomentItemTypeHelper);
                mItemViewDatas.add(momentItem);
            } else if (type.equals("ad")) {
                AdInfo adInfo = new AdInfo();
                adInfo.setAd(momentItemDto.getAd());
                adInfo.setItemViewTypeHelper(mAdTypeHelper);
                mItemViewDatas.add(adInfo);
            }
        }

        mPage++;
        mAdapter.setListData(mItemViewDatas);
        if (!isLocation) {
            if (mItemViewDatas.size() == 0) {
                mLayoutEmpty.setVisibility(View.VISIBLE);
            } else {
                mLayoutEmpty.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public void onLoadMore() {
        loadMoments();
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        loadMoments();
    }

    @Override
    protected void onUpdateReceiver(Intent intent) {
        super.onUpdateReceiver(intent);
        String action = intent.getAction();
        if (action.equals(AppConstants.ACTION_MOMENT_UPDATE)) {
            mRefresh.autoRefresh();
        } else if (action.equals(AppConstants.ACTION_MOMENT_DELETE)) {
            long id = intent.getLongExtra("id", 0);
            if (mAdapter == null) {
                return;
            }
            List<ItemViewTypeHelperManager.ItemViewData> itemViewDatas = mAdapter.getListData();
            for (int i = 0; i < itemViewDatas.size(); i++) {
                ItemViewTypeHelperManager.ItemViewData itemViewData = itemViewDatas.get(i);
                if (itemViewData instanceof MomentItem) {
                    MomentItem momentItem = (MomentItem) itemViewData;
                    if (momentItem.getMomentDto().getId() == id) {
                        mAdapter.getListData().remove(i);
                        mAdapter.notifyItemRemoved(i);
                        mAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int margin = DeviceUtils.dipToPX(getActivity(), 4);
            outRect.bottom = margin;
            int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
            int position = parent.getChildAdapterPosition(view);

            if (mAdapter != null && mAdapter.getListData().size() > 0) {
                ItemViewTypeHelperManager.ItemViewData itemViewData = mAdapter.getListData().get(position);
                if (itemViewData instanceof MomentItem) {
                    int adPosition = getNearestAdPosition(position);
                    if ((position - adPosition) % spanCount == 0) {
                        outRect.right = margin;
                    } else {
                        outRect.left = margin;
                        outRect.right = margin;
                    }
                }
            }
        }
        private int getNearestAdPosition(int position) {
            for (int i = position; i >= 0; i--) {
                ItemViewTypeHelperManager.ItemViewData viewData = mAdapter.getListData().get(i);
                if (viewData instanceof AdInfo) {
                    return i;
                }
            }
            return -1;
        }
    }
}
