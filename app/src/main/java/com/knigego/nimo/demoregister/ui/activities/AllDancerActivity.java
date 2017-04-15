package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crooods.common.domain.view.BizData4Page;
import com.crooods.common.protocol.ResponseT;
import com.crooods.wd.dto.DancerDto;
import com.crooods.wd.service.IDancerStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.ui.model.DancerItem;
import com.knigego.nimo.demoregister.ui.typehelper.DancerItemTypeHelper;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.uimanager.VarietyTypeAdapter;
import com.knigego.nimo.demoregister.util.DeviceUtils;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AllDancerActivity extends BaseActivity {

    @Bind(R.id.recyclerView_all_dancer)
    RecyclerViewPager mRecyclerViewAllDancer;

    private List<ItemViewTypeHelperManager.ItemViewData> mData = new ArrayList<>();
    private int mPage = 1;
    private int totalCount;
    private boolean isLoadingMore;

    private DancerItemTypeHelper mDancerItemTypeHelper;
    private VarietyTypeAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ItemViewTypeHelperManager mItemViewTypeHelperManager;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_all_dancer);
        ButterKnife.bind(this);
        setTitle(R.string.title_all_dancer);
        init();
        mPage = 1;
        loadData();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int left = mRecyclerViewAllDancer.getPaddingLeft();
        int right = mRecyclerViewAllDancer.getPaddingRight();
        Log.i("/////////////////////", "onWindowFocusChanged: " + left + " ,,,,,, " + right);
    }

    private void init() {
        mRecyclerViewAllDancer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager == null) {
                    return;
                }

                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();

                if (lastVisibleItem >= totalItemCount - 1 && dx > 0) {
                    if (mAdapter != null && mData.size() < totalCount) {
                        if (!isLoadingMore) {
                            loadMore();
                        }
                    }
                }
            }
        });

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewAllDancer.setLayoutManager(mLayoutManager);
        mRecyclerViewAllDancer.setHasFixedSize(true);
        mRecyclerViewAllDancer.addItemDecoration(new SpaceItemDecoration());
        mAdapter = new VarietyTypeAdapter(getItemViewTypeHelperManager(), mData);
        mRecyclerViewAllDancer.setAdapter(mAdapter);
    }

    private ItemViewTypeHelperManager getItemViewTypeHelperManager() {
        if (mItemViewTypeHelperManager == null) {
            mItemViewTypeHelperManager = new ItemViewTypeHelperManager();

            mDancerItemTypeHelper = new DancerItemTypeHelper(this, R.layout.view_dancer_item);
            mItemViewTypeHelperManager.addType(mDancerItemTypeHelper);
        }
        return mItemViewTypeHelperManager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_serch, menu);
        return true;
    }

    @Override
    protected boolean _onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, DancerSearchActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    private void loadMore() {
        isLoadingMore = true;
        loadData();
    }

    private void loadData() {
        if (mPage == 1) {
            showProgress(R.string.loading);
        }
        IDancerStubService service = createApi(IDancerStubService.class);
        service.dancers(AppPref.getInstance().getAccessToken(), "", 0, 20, mPage,
                new RetrofitUtil.ActivityCallback<ResponseT<BizData4Page<DancerDto>>>(this) {
                    @Override
                    public void success(ResponseT<BizData4Page<DancerDto>> bizData4PageResponseT, Response response) {
                        hideProgress();
                        super.success(bizData4PageResponseT, response);
                        if (bizData4PageResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            List<DancerDto> dancerDtos = bizData4PageResponseT.getBizData().getRows();
                            totalCount = bizData4PageResponseT.getBizData().getRecords();
                            updateUI(dancerDtos);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideProgress();
                        super.failure(error);
                    }
                });
    }

    private void updateUI(List<DancerDto> dancerDtos) {
        if (dancerDtos == null) {
            return;
        }
        List<DancerItem> dancerItems = new ArrayList<>();
        for (int i = 0; i < dancerDtos.size(); i++) {
            DancerDto dancerDto = dancerDtos.get(i);
            DancerItem dancerItem = new DancerItem();
            dancerItem.setDancerDto(dancerDto);
            dancerItem.setItemViewTypeHelper(mDancerItemTypeHelper);
            dancerItems.add(dancerItem);
        }

        if (mPage == 1) {
            mData.clear();
        }

        mData.addAll(dancerItems);
        mAdapter.setListData(mData);
        mPage++;
        isLoadingMore = false;
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = DeviceUtils.dipToPX(AllDancerActivity.this, 15);
        }
    }
}
