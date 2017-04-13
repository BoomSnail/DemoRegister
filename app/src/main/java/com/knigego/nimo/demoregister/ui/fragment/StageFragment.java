package com.knigego.nimo.demoregister.ui.fragment;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.canyinghao.canrefresh.CanRefreshLayout;
import com.crooods.common.domain.view.BizData4Page;
import com.crooods.common.protocol.ResponseT;
import com.crooods.wd.dto.StarShowRangeDto;
import com.crooods.wd.dto.post.StageDto;
import com.crooods.wd.service.IStageStubService;
import com.crooods.wd.service.IStarStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.base.BaseFragment;
import com.knigego.nimo.demoregister.ui.model.StageItem;
import com.knigego.nimo.demoregister.ui.model.SuperStarItem;
import com.knigego.nimo.demoregister.ui.model.SuperStarWrapper;
import com.knigego.nimo.demoregister.ui.typehelper.StageItemHelper;
import com.knigego.nimo.demoregister.ui.typehelper.StageSuperStarTypeHelper;
import com.knigego.nimo.demoregister.ui.typehelper.SuperStarTitleHelper;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.uimanager.VarietyTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class StageFragment extends BaseFragment implements CanRefreshLayout.OnRefreshListener, CanRefreshLayout.OnLoadMoreListener {

    public static final String TAG = StageFragment.class.getSimpleName();
    @Bind(R.id.layout_empty)
    LinearLayout mLayoutEmpty;
    @Bind(R.id.layout_first_loading)
    LinearLayout mLayoutFirstLoading;
    @Bind(R.id.can_content_view)
    RecyclerView mCanContentView;
    @Bind(R.id.refresh)
    CanRefreshLayout mRefresh;

    private int mPage = 1;
    private int mPageCount = 10;

    private int totalCount = 0;

    private boolean isSuperStarsRefreshComplete = false;
    private boolean isStageRefreshComplete = false;

    private List<StageItem> mStageItems = new ArrayList<>();

    private SuperStarWrapper mSuperStarWrapper;

    private SuperStarTitleHelper mSuperStarTitleHelper;
    private StageSuperStarTypeHelper mStageSuperStarTypeHelper;
    private StageItemHelper mStageItemhelper;

    private VarietyTypeAdapter mAdapter;
    private ItemViewTypeHelperManager mManager;

    private ItemViewTypeHelperManager.ItemViewData mSuperStarTitle;
    private List<ItemViewTypeHelperManager.ItemViewData> mDatas = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_stage;
    }

    @Override
    protected void onCreateView(View view, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        mCanContentView.setLayoutManager(layoutManager);
        mCanContentView.setItemAnimator(new DefaultItemAnimator());
        mRefresh.setOnRefreshListener(this);
        mRefresh.setOnLoadMoreListener(this);
        mRefresh.setStyle(0, 0);

        initUI();

        List<StageDto> stageDtos = AppPref.getInstance().getStages();
        if (stageDtos != null) {
            updateStages(stageDtos, true);
        }
        if (stageDtos != null && stageDtos.size() == 0) {
            mPage = 1;
            loadSuperStars();
            loadStages();
            mLayoutFirstLoading.setVisibility(View.VISIBLE);
        } else {
            mLayoutFirstLoading.setVisibility(View.GONE);
            mRefresh.autoRefresh();
        }
    }

    private void loadStages() {
        isStageRefreshComplete = false;
        IStageStubService stageStubService = createApi(IStageStubService.class);
        stageStubService.stages(AppPref.getInstance().getAccessToken(), "", 0, 0, mPageCount, mPage,
                new RetrofitUtil.ActivityCallback<ResponseT<BizData4Page<StageDto>>>(getContext()) {
                    @Override
                    public void success(ResponseT<BizData4Page<StageDto>> bizData4PageResponseT, Response response) {
                        isStageRefreshComplete = true;
                        onRefreshComplete();
                        super.success(bizData4PageResponseT, response);
                        if (bizData4PageResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            BizData4Page<StageDto> stageDtoBizData4Page = bizData4PageResponseT.getBizData();
                            totalCount = stageDtoBizData4Page.getRecords();
                            List<StageDto> stageDtos = stageDtoBizData4Page.getRows();
                            if (mPage == 1) {
                                AppPref.getInstance().saveStages(stageDtos);
                            }

                            updateStages(stageDtos, false);
                            if (mAdapter != null && mDatas.size() >= totalCount) {
                                mRefresh.setLoadMoreEnabled(false);
                            } else {
                                mRefresh.setLoadMoreEnabled(true);
                            }

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        isStageRefreshComplete = true;
                        onRefreshComplete();
                        super.failure(error);
                    }
                });

    }

    private void onRefreshComplete() {
        if (isSuperStarsRefreshComplete && isStageRefreshComplete) {
            if (mRefresh != null) {
                mRefresh.refreshComplete();
                mRefresh.loadMoreComplete();
            }
            if (mLayoutFirstLoading != null) {
                mLayoutFirstLoading.setVisibility(View.GONE);
            }
        }
    }

    private void loadSuperStars() {
        isSuperStarsRefreshComplete = false;
        IStarStubService starStubService = createApi(IStarStubService.class);
        starStubService.showRange(AppPref.getInstance().getAccessToken(),
                new RetrofitUtil.ActivityCallback<ResponseT<List<StarShowRangeDto>>>(getContext()) {
                    @Override
                    public void success(ResponseT<List<StarShowRangeDto>> listResponseT, Response response) {
                        isSuperStarsRefreshComplete = true;
                        onRefreshComplete();
                        super.success(listResponseT, response);

                        if (listResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            List<StarShowRangeDto> starShowRangeDtos = listResponseT.getBizData();
                            updateStarShowRange(starShowRangeDtos);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        isSuperStarsRefreshComplete = true;
                        onRefreshComplete();
                        super.failure(error);
                    }
                });
    }

    private void updateStarShowRange(List<StarShowRangeDto> starShowRangeDtos) {
        if (starShowRangeDtos == null || starShowRangeDtos.size() == 0) {
            mSuperStarTitle = null;
            mSuperStarWrapper = null;
            updateUI(false);
            return;
        }

        mSuperStarWrapper = new SuperStarWrapper();
        List<SuperStarItem> superStarItems = new ArrayList<>();
        for (int i = 0; i < starShowRangeDtos.size(); i++) {
            SuperStarItem superStarItem = new SuperStarItem();
            superStarItem.setStarShowRangeDto(starShowRangeDtos.get(i));
            superStarItems.add(superStarItem);
        }

        mSuperStarWrapper.setSuperStarItems(superStarItems);
        mSuperStarWrapper.setItemViewTypeHelper(mStageSuperStarTypeHelper);

        mSuperStarTitle = new ItemViewTypeHelperManager.ItemViewData();
        mSuperStarTitle.setItemViewTypeHelper(mSuperStarTitleHelper);
        updateUI(false);
    }

    private void initUI() {
        mAdapter = new VarietyTypeAdapter(getManager(), mDatas);
        mCanContentView.setAdapter(mAdapter);
    }

    private void updateStages(List<StageDto> stageDtos, boolean isLocation) {
        mDatas.clear();
        if (mSuperStarTitle != null) {
            mDatas.add(mSuperStarTitle);
        }
        List<StageItem> stageItems = new ArrayList<>();

        for (int i = 0; i < stageDtos.size(); i++) {
            stageItems.add(new StageItem(stageDtos.get(i), mStageItemhelper));
        }

        if (mPage == 1) {
            mStageItems.clear();
        } else {

        }
        mStageItems.addAll(stageItems);
        mPage++;
        updateUI(isLocation);
    }

    private void updateUI(boolean isLocation) {
        mDatas.clear();
        if (mSuperStarTitle != null) {
            mDatas.add(mSuperStarTitle);
        }
        if (mSuperStarWrapper != null) {
            mDatas.add(mSuperStarWrapper);
        }
        if (mStageItems != null) {
            mDatas.addAll(mStageItems);
        }

        mAdapter.setListData(mDatas);

        if (!isLocation) {
            if (mDatas.size() == 0) {
                mLayoutEmpty.setVisibility(View.VISIBLE);
            } else {
                mLayoutEmpty.setVisibility(View.GONE);
            }
        }
    }


    private ItemViewTypeHelperManager getManager() {
        if (mManager == null) {
            mManager = new ItemViewTypeHelperManager();

            mSuperStarTitleHelper = new SuperStarTitleHelper(getContext(), R.layout.view_stage_title_item);
            mManager.addType(mSuperStarTitleHelper);

            mStageSuperStarTypeHelper = new StageSuperStarTypeHelper(getContext(), R.layout.view_super_star);
            mManager.addType(mStageSuperStarTypeHelper);

            mStageItemhelper = new StageItemHelper(getContext(), R.layout.view_stage_item, false);
            mManager.addType(mStageItemhelper);
        }
        return mManager;
    }

    @Override
    protected void onUpdateReceiver(Intent intent) {
        super.onUpdateReceiver(intent);
        String action = intent.getAction();
        if (action.equals(AppConstants.ACTION_STAGE_UPDATE)) {
            mRefresh.autoRefresh();
        } else if (action.equals(AppConstants.ACTION_STAGE_DELETE)) {
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
                    updateUI(false);
                    return;
                }

            }
        }
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();

        ButterKnife.unbind(this);
    }

    @Override
    public void onLoadMore() {
        loadStages();
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        loadSuperStars();
        loadStages();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.text_reload)
    public void onViewClicked() {
        mPage = 1;
        loadSuperStars();
        loadStages();
        mLayoutEmpty.setVisibility(View.GONE);
        mLayoutFirstLoading.setVisibility(View.VISIBLE);
    }

    public static class SpaceBottomItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceBottomItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;
        }
    }
}
