package com.knigego.nimo.demoregister.ui.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.uimanager.VarietyTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseRefreshActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.recyclerView)
    public RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    public SwipeRefreshLayout mRefreshLayout;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected boolean isLoadingMore = false;
    protected VarietyTypeAdapter mAdapter;
    protected List<ItemViewTypeHelperManager.ItemViewData> mDatas = new ArrayList<>();
    protected ItemViewTypeHelperManager mItemViewTypeHelperManager;
    protected int totalCount = 0;
    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base_refresh);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager instanceof LinearLayoutManager) {
                    int lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                    int totalItemCount = mLayoutManager.getItemCount();
                    //lastVisibleItem >= totalItemCount - 1 表示剩下1个item自动加载，各位自由选择
                    // dy>0 表示向下滑动
                    if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                        if (mAdapter != null && mDatas.size() < totalCount) {
                            if (!isLoadingMore) {
                                loadMore();
                            }
                        }
                    }
                } else if (mLayoutManager instanceof GridLayoutManager) {
                    int lastVisibleItem = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                    int totalItemCount = mLayoutManager.getItemCount();
                    //lastVisibleItem >= totalItemCount - 2 表示剩下1个item自动加载，各位自由选择
                    // dy>0 表示向下滑动
                    if (lastVisibleItem >= totalItemCount - ((GridLayoutManager) mLayoutManager).getSpanCount() && dy > 0) {
                        if (mAdapter != null && mDatas.size() < totalCount) {
                            if (!isLoadingMore) {
                                loadMore();
                            }
                        }
                    }
                }
            }
        });

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(this);
    }

    protected void refreshComplete(){
        if (mRefreshLayout != null && mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
            isLoadingMore = false;
        }
    }

    public void loadMore() {
        isLoadingMore = true;
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(true);
        }
    }

    protected void initListView(){
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);//这里用线性显示 类似于listview
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    protected void initGridView(int spanCount) {
        mLayoutManager = new GridLayoutManager(this, spanCount);
        mRecyclerView.setLayoutManager(mLayoutManager);//这里用线性宫格显示 类似于grid view
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity()));
//        ((GridLayoutManager)mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {//header设置，如果是第一条则为header
//                return position == 0 ? ((GridLayoutManager) mLayoutManager).getSpanCount() : 1;
//            }
//        });
    }

}