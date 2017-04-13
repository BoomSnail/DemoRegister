package com.knigego.nimo.demoregister.ui.base;


import android.os.Bundle;
import android.provider.Contacts;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.canyinghao.canrefresh.CanRefreshLayout;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.uimanager.VarietyTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseRefreshFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{


    @Bind(R.id.recyclerView)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    protected SwipeRefreshLayout mRefreshLayout;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected boolean isLoadIngMore = false;
    protected List<ItemViewTypeHelperManager.ItemViewData> mDatas = new ArrayList<>();
    protected ItemViewTypeHelperManager mItemViewTypeHelperManager;
    protected VarietyTypeAdapter mAdapter;
    protected int totalCount = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_refresh;
    }

    @Override
    protected void onCreateView(View view, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        initUI();
        _onCreateView(view);
    }

    protected abstract void _onCreateView(View view);

    protected void initUI() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager == null) {
                    return;
                }
                if (mLayoutManager instanceof LinearLayoutManager) {
                    int lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                    int totalItemCount = mLayoutManager.getItemCount();

                    if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                        if (mAdapter != null && mDatas.size() < totalCount) {
                            if (!isLoadIngMore) {
                                loadMore();
                            }
                        }
                    }
                } else if (mLayoutManager instanceof GridLayoutManager) {
                    int lastVisibleItem = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                    int totalItemCount = mLayoutManager.getItemCount();

                    if (lastVisibleItem >= totalItemCount - ((GridLayoutManager) mLayoutManager).getSpanCount() && dy >0) {
                        if (mAdapter != null && mDatas.size() < totalCount) {
                            if (!isLoadIngMore) {
                                loadMore();
                            }
                        }
                    }
                }
            }
        });
        mRefreshLayout.setColorSchemeResources(R.color.refresh_color);
        mRefreshLayout.setOnRefreshListener(this);
    }

    protected void initGridView(int spanCount){
        mLayoutManager = new GridLayoutManager(getActivity(),spanCount);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    public void loadMore() {
        isLoadIngMore = true;
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(true);
        }
    }

    protected void refreshComplete(){
        if (mRefreshLayout != null && mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
            isLoadIngMore = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
