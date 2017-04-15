package com.knigego.nimo.demoregister.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crooods.common.domain.view.BizData4Page;
import com.crooods.common.protocol.ResponseT;
import com.crooods.wd.domain.AppUser;
import com.crooods.wd.service.IDancerStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.ui.model.DancerInfo;
import com.knigego.nimo.demoregister.ui.typehelper.SearchEmptyTypeHelper;
import com.knigego.nimo.demoregister.ui.typehelper.SearchItemTypeHelper;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.uimanager.VarietyTypeAdapter;
import com.knigego.nimo.demoregister.util.AndroidBug5497Workaround;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DancerSearchActivity extends BaseActivity {

    @Bind(R.id.edit_search_content)
    EditText mEditSearchContent;
    @Bind(R.id.recyclerView_dancer)
    RecyclerView mRecyclerViewDancer;
    @Bind(R.id.btn_search)
    Button mBtnSearch;
    @Bind(R.id.text_cancel)
    TextView mTextCancel;

    private ItemViewTypeHelperManager mItemViewTypeHelperManager;
    private SearchEmptyTypeHelper mSearchEmptyTypeHelper;
    private SearchItemTypeHelper mSearchItemTypeHelper;
    private VarietyTypeAdapter mAdapter;
    private List<ItemViewTypeHelperManager.ItemViewData> mDatas = new ArrayList<>();
    private int mPage = 1;
    private int totalCount;
    private LinearLayoutManager mLayoutManager;
    private boolean isLoadingMore = false;
    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_dancer_search);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);
        hideToolbar(true);
        init();
        mEditSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
    }

    private void init() {
        mRecyclerViewDancer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager == null) {
                    return;
                }

                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
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
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewDancer.setLayoutManager(mLayoutManager);
        mAdapter = new VarietyTypeAdapter(getItemHelperManager(), mDatas);
        mRecyclerViewDancer.setAdapter(mAdapter);
    }

    private void loadMore() {

        isLoadingMore = true;
        loadData();
    }

    private void search() {
    }

    public ItemViewTypeHelperManager getItemHelperManager() {
        if (mItemViewTypeHelperManager == null) {
            mItemViewTypeHelperManager = new ItemViewTypeHelperManager();

            mSearchEmptyTypeHelper = new SearchEmptyTypeHelper(this, R.layout.view_search_empty);
            mItemViewTypeHelperManager.addType(mSearchEmptyTypeHelper);

            mSearchItemTypeHelper = new SearchItemTypeHelper(this, R.layout.view_search_item);
            mItemViewTypeHelperManager.addType(mSearchItemTypeHelper);
        }
        return mItemViewTypeHelperManager;
    }

    @OnClick({R.id.text_cancel, R.id.btn_search})
    public void search(View view) {
        switch (view.getId()) {
            case R.id.text_cancel:
                finish();
                break;
            case R.id.btn_search:
                String content = mEditSearchContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(this,R.string.search_tip,Toast.LENGTH_SHORT).show();
                    return;
                }

                mPage = 1;
                loadData();
                break;
        }
    }

    private void loadData() {
        String content = mEditSearchContent.getText().toString().trim();
        if (mPage == 1) {
            showProgress(R.string.loading);
        }

        IDancerStubService service = createApi(IDancerStubService.class);
        service.query(AppPref.getInstance().getAccessToken(),content,0,20,mPage,
                new RetrofitUtil.ActivityCallback<ResponseT<BizData4Page<AppUser>>>(this){
                    @Override
                    public void success(ResponseT<BizData4Page<AppUser>> bizData4PageResponseT, Response response) {
                        hideProgress();
                        super.success(bizData4PageResponseT, response);
                        if (bizData4PageResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            List<AppUser> appUsers = bizData4PageResponseT.getBizData().getRows();
                            totalCount = bizData4PageResponseT.getBizData().getRecords();
                            updateUI(appUsers);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideProgress();
                        super.failure(error);
                    }
                });
    }

    private void updateUI(List<AppUser> appUsers) {
        if (appUsers == null) {
            return;
        }

        List<DancerInfo> dancerInfos = new ArrayList<>();
        for (int i = 0; i < appUsers.size(); i++) {
            AppUser appUser = appUsers.get(i);
            DancerInfo dancerInfo = new DancerInfo();
            dancerInfo.setAppUser(appUser);
            dancerInfo.setItemViewTypeHelper(mSearchItemTypeHelper);
            dancerInfos.add(dancerInfo);
        }

        if (mPage == 1) {
            mDatas.clear();
        }
        mDatas.addAll(dancerInfos);

        if (mDatas.size() == 0) {
            ItemViewTypeHelperManager.ItemViewData itemViewData = new ItemViewTypeHelperManager.ItemViewData();
            itemViewData.setItemViewTypeHelper(mSearchEmptyTypeHelper);
            mDatas.add(itemViewData);
        }
        mAdapter.setListData(mDatas);

    }
}
