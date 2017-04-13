package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crooods.common.domain.view.BizData4Page;
import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.dto.StringWrapper;
import com.crooods.wd.dto.ActionCommentCreateDto;
import com.crooods.wd.dto.post.MomentItemDto;
import com.crooods.wd.dto.post.StageDto;
import com.crooods.wd.dto.version.ActionCommentDto;
import com.crooods.wd.enums.MediaTypeEnum;
import com.crooods.wd.enums.PostTypeEnum;
import com.crooods.wd.service.IActionStubService;
import com.crooods.wd.service.IMomentStubService;
import com.crooods.wd.service.IStageStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.base.BaseRefreshActivity;
import com.knigego.nimo.demoregister.ui.model.CommentItem;
import com.knigego.nimo.demoregister.ui.model.ShareInfo;
import com.knigego.nimo.demoregister.ui.model.StageItem;
import com.knigego.nimo.demoregister.ui.typehelper.CommentCountItemTypeHelper;
import com.knigego.nimo.demoregister.ui.typehelper.CommentItemTypeHelper;
import com.knigego.nimo.demoregister.ui.typehelper.ShareViewTypeHelper;
import com.knigego.nimo.demoregister.ui.typehelper.StageDetailTypeHelper;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.uimanager.VarietyTypeAdapter;
import com.knigego.nimo.demoregister.util.AndroidBug5497Workaround;
import com.knigego.nimo.demoregister.util.AudioManager;
import com.knigego.nimo.demoregister.util.BrodcastHelper;
import com.knigego.nimo.demoregister.util.CommonUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class StageDetailActivity extends BaseRefreshActivity implements AndroidBug5497Workaround.OnKeyboardChangeListener {

    @Bind(R.id.layout_empty)
    RelativeLayout mEmptyLayout;
    @Bind(R.id.layout_comment_bar)
    LinearLayout mCommentLayout;
    @Bind(R.id.edit_comment)
    EditText mCommentEdit;
    @Bind(R.id.btn_send)
    Button mSendButton;

    private int mType = AppConstants.TYPE_STAGE;
    private boolean comment;

    private AudioManager mAudioManager;
    private int mPage = 1;
    private int mPageCount = 10;

    private StageDetailTypeHelper mStageDetailTypeHelper;
    private ShareViewTypeHelper mShareViewTypeHelper;
    private CommentCountItemTypeHelper mComemntCountItemTypeHelper;
    private CommentItemTypeHelper mCommentItemTypeHelper;

    private StageDto mStageDto;
    private StageItem mStageItem;
    private ShareInfo mShareInfo;
    private CommentCountItemTypeHelper.CommentCountData mComemntCountData;
    private List<CommentItem> mCommentItems = new ArrayList<>();

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        super._onCreate(savedInstanceState);
        AndroidBug5497Workaround.assistActivity(this, this);
        initListView();
        setTitle(R.string.title_stage_detail);
        getIntentValues();
        mEmptyLayout.setVisibility(View.GONE);
        if (mType == AppConstants.TYPE_STAGE && mStageDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
            if (mStageDto.getVideoData().getAudio() != null
                    && !TextUtils.isEmpty(mStageDto.getVideoData().getAudio().getUrl())) {
                mAudioManager = new AudioManager();
            }
        }
        initUI();
        updateUI();
        if (mStageItem.getStageDto().getMediaType().equals(MediaTypeEnum.VIDEO.getCode())
                && mType == AppConstants.TYPE_STAGE
                && mStageItem.getStageDto().getVideoData().getAudio() != null
                && !TextUtils.isEmpty(mStageItem.getStageDto().getVideoData().getAudio().getUrl())) {
            mAudioManager.bindService(this);
            mAudioManager.setAudioPath(mStageItem.getStageDto().getVideoData().getAudio().getUrl());
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mPage = 1;
                loadComments();
            }
        }, 300);
    }

    private ItemViewTypeHelperManager getItemManager() {
        if (mItemViewTypeHelperManager == null) {
            mItemViewTypeHelperManager = new ItemViewTypeHelperManager();

            mStageDetailTypeHelper = new StageDetailTypeHelper(this, R.layout.view_stage_detail, mAudioManager, mType);
            mItemViewTypeHelperManager.addType(mStageDetailTypeHelper);

            mShareViewTypeHelper = new ShareViewTypeHelper(this, R.layout.view_share_item);
            mItemViewTypeHelperManager.addType(mShareViewTypeHelper);

            mComemntCountItemTypeHelper = new CommentCountItemTypeHelper(this, R.layout.view_comment_count_item);
            mItemViewTypeHelperManager.addType(mComemntCountItemTypeHelper);

            mCommentItemTypeHelper = new CommentItemTypeHelper(this, R.layout.view_comment_item, -1);
            mItemViewTypeHelperManager.addType(mCommentItemTypeHelper);

        }
        return mItemViewTypeHelperManager;
    }

    private void loadComments() {
        IActionStubService service = createApi(IActionStubService.class);
        service.comments(AppPref.getInstance().getAccessToken(), mType == AppConstants.TYPE_STAGE ? "stages" : "moments",
                mStageItem.getStageDto().getId(), 0, mPageCount, mPage,
                new RetrofitUtil.ActivityCallback<ResponseT<BizData4Page<ActionCommentDto>>>(this) {
                    @Override
                    public void success(ResponseT<BizData4Page<ActionCommentDto>> bizData4PageResponseT, Response response) {
                        super.success(bizData4PageResponseT, response);
                        refreshComplete();
                        if (bizData4PageResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            BizData4Page<ActionCommentDto> commentDtoBizData4Page = bizData4PageResponseT.getBizData();
                            totalCount = commentDtoBizData4Page.getRecords();
                            List<ActionCommentDto> commentDtos = commentDtoBizData4Page.getRows();
                            updateComments(commentDtos);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        refreshComplete();
                    }
                });
    }

    private void updateComments(List<ActionCommentDto> commentDtos) {
        if (commentDtos == null) {
            return;
        }
        List<CommentItem> commentItems = new ArrayList<>();
        for (int i = 0; i < commentDtos.size(); i++) {
            commentItems.add(new CommentItem(commentDtos.get(i), mCommentItemTypeHelper));
        }
        if (mPage == 1) {
            mCommentItems.clear();
        } else {

        }
        mCommentItems.addAll(commentItems);
        mPage++;
        mComemntCountData = new CommentCountItemTypeHelper.CommentCountData();
        mComemntCountData.setCount(totalCount);
        mComemntCountData.setItemViewTypeHelper(mComemntCountItemTypeHelper);
        updateUI();
    }

    private void updateUI() {
        mDatas.clear();
        if (mStageItem != null) {
            mDatas.add(mStageItem);
        }
        if (mShareInfo != null) {
            mDatas.add(mShareInfo);
        }
        if (mComemntCountData != null) {
            mDatas.add(mComemntCountData);
        }
        if (mCommentItems != null && mCommentItems.size() > 0) {
            mDatas.addAll(mCommentItems);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void getIntentValues() {
        Intent intent = getIntent();
        comment = intent.getBooleanExtra(AppConstants.IS_COMMENT, false);
        mType = intent.getIntExtra(AppConstants.TYPE, AppConstants.TYPE_STAGE);
        mStageDto = (StageDto) intent.getSerializableExtra(AppConstants.STAGE_ITEM);
    }

    private void initUI() {
        mAdapter = new VarietyTypeAdapter(getItemManager(), mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mCommentLayout.setVisibility(View.VISIBLE);
        mSendButton.setEnabled(false);
        mCommentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mStageItem = new StageItem(mStageDto, mStageDetailTypeHelper);
        mShareInfo = new ShareInfo();
        mShareInfo.setType(mType);
        mShareInfo.setStageDto(mStageDto);
        mShareInfo.setItemViewTypeHelper(mShareViewTypeHelper);

        if (comment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCommentEdit.requestFocus();
                    CommonUtils.showSoftInputMethod(StageDetailActivity.this);
                }
            }, 500);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAudioManager != null) {
            mAudioManager.destroy(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        if (mStageDto != null) {
            if (mStageDto.getUser().getId() == AppPref.getInstance().getUserId()) {
                menu.getItem(0).setVisible(true);
            } else {
                menu.getItem(0).setVisible(false);
            }
        }
        return true;
    }

    @Override
    public void loadMore() {
        super.loadMore();
        loadComments();
    }

    @Override
    protected boolean _onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteAction();
                break;
            default:
                break;
        }
        return true;
    }

    private void deleteAction() {
        showProgress(R.string.loading_delete);
        if (mType == AppConstants.TYPE_STAGE) {
            IStageStubService stageStubService = createApi(IStageStubService.class);
            stageStubService.stageDelete(AppPref.getInstance().getAccessToken(),
                    mStageDto.getId(), new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(this) {
                        @Override
                        public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                            super.success(stringWrapperResponseT, response);
                            hideProgress();
                            if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                                stageDelete();
                                BrodcastHelper.sendDeleteStageAction(StageDetailActivity.this, mStageDto.getId());
                                finish();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            super.failure(error);
                            hideProgress();
                        }
                    });
        } else if (mType == AppConstants.TYPE_MOMENT) {
            IMomentStubService stubService = createApi(IMomentStubService.class);
            stubService.momentDelete(AppPref.getInstance().getAccessToken(), mStageDto.getId(),
                    new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(this) {
                        @Override
                        public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                            super.success(stringWrapperResponseT, response);
                            hideProgress();
                            if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                                momentDelete();
                                BrodcastHelper.sendDeleteMomentAction(StageDetailActivity.this, mStageDto.getId());
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            super.failure(error);
                            hideProgress();
                        }
                    });
        }
    }

    private void momentDelete() {
        List<MomentItemDto> myMomentItemDtos = AppPref.getInstance().getMyMoments();
        for (int i = 0; i < myMomentItemDtos.size(); i++) {
            if (myMomentItemDtos.get(i).getMoment() != null
                    && myMomentItemDtos.get(i).getMoment().getId() == mStageDto.getId()) {
                myMomentItemDtos.remove(i);
                break;
            }
        }
        AppPref.getInstance().saveMyMoments(myMomentItemDtos);

        List<MomentItemDto> momentItemDtos = AppPref.getInstance().getMoments();
        for (int i = 0; i < momentItemDtos.size(); i++) {
            if (momentItemDtos.get(i).getMoment() != null
                    && momentItemDtos.get(i).getMoment().getId() == mStageDto.getId()) {
                momentItemDtos.remove(i);
                break;
            }
        }
        AppPref.getInstance().saveMoments(momentItemDtos);
    }

    private void stageDelete() {
        List<StageDto> myStageDtos = AppPref.getInstance().getMyStages();
        for (int i = 0; i < myStageDtos.size(); i++) {
            if (myStageDtos.get(i).getId() == mStageDto.getId()) {
                myStageDtos.remove(i);
                break;
            }
        }
        AppPref.getInstance().saveMyStages(myStageDtos);

        List<StageDto> stageDtos = AppPref.getInstance().getStages();
        for (int i = 0; i < stageDtos.size(); i++) {
            if (stageDtos.get(i).getId() == mStageDto.getId()) {
                stageDtos.remove(i);
                break;
            }
        }

        AppPref.getInstance().saveStages(stageDtos);

    }

    @OnClick(R.id.btn_send)
    public void sendComment() {
        showProgress(R.string.loading_comment);
        IActionStubService service = createApi(IActionStubService.class);
        ActionCommentCreateDto commentCreateDto = new ActionCommentCreateDto();
        commentCreateDto.setPostId(mStageItem.getStageDto().getId());
        commentCreateDto.setContent(mCommentEdit.getText().toString());

        if (mType == AppConstants.TYPE_STAGE) {
            commentCreateDto.setPostType(PostTypeEnum.STAGE.getCode());
        } else if (mType == AppConstants.TYPE_MOMENT) {
            commentCreateDto.setPostType(PostTypeEnum.MOMOENT.getCode());
        }
        commentCreateDto.setUserId(mStageItem.getStageDto().getUser().getId());
        service.comment(AppPref.getInstance().getAccessToken(), mType == AppConstants.TYPE_STAGE ? "stages" : "moments",
                mStageItem.getStageDto().getId(), commentCreateDto, new RetrofitUtil.ActivityCallback<ResponseT<ActionCommentDto>>(this) {
                    @Override
                    public void success(ResponseT<ActionCommentDto> actionCommentDtoResponseT, Response response) {
                        super.success(actionCommentDtoResponseT, response);
                        hideProgress();
                        if (actionCommentDtoResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            mCommentEdit.setText("");
                            CommonUtils.hideSoftInput(StageDetailActivity.this, mCommentEdit);
                            mRefreshLayout.setRefreshing(true);
                            onRefresh();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        hideProgress();
                    }
                });

    }

    @Override
    public void onRefresh() {
        mPage = 1;
        loadComments();
    }

    @Override
    public void onKeyboardShow() {
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (mDatas != null && mDatas.size() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.smoothScrollToPosition(mDatas.size());
                }
            }, 300);
        }
    }

    @Override
    public void onKeyboardHide() {

    }

    //edittext 获取焦点，谈起软键盘
    public void editFocus() {
        mCommentEdit.requestFocus();
        CommonUtils.showSoftInputMethod(this);
        scrollToBottom();
    }

    public void reportOperate(StageDto stageDto) {
        DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.popup_stage_detail_operate))
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.text_cache_video:
                                break;
                            case R.id.text_cache_audio:
                                break;
                            case R.id.text_report:
                                break;
                            case R.id.cancel:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .create();
        View view = dialogPlus.getHolderView();

        TextView cacheVideoText = (TextView) view.findViewById(R.id.text_cache_video);
        View videoDivider = view.findViewById(R.id.view_cache_video_divider);
        TextView cacheAudioText = (TextView) view.findViewById(R.id.text_cache_audio);
        View audioDivider = view.findViewById(R.id.view_cache_audio_divider);
        if (mType == AppConstants.TYPE_STAGE) {
            if (stageDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
                cacheVideoText.setVisibility(View.VISIBLE);
                videoDivider.setVisibility(View.VISIBLE);
                if (stageDto.getVideoData().getAudio() == null
                        || TextUtils.isEmpty(stageDto.getVideoData().getAudio().getUrl())) {
                    cacheAudioText.setVisibility(View.GONE);
                    audioDivider.setVisibility(View.GONE);
                } else {
                    cacheAudioText.setVisibility(View.VISIBLE);
                    audioDivider.setVisibility(View.VISIBLE);
                }
            } else {
                cacheVideoText.setVisibility(View.GONE);
                videoDivider.setVisibility(View.GONE);
                cacheAudioText.setVisibility(View.GONE);
                audioDivider.setVisibility(View.GONE);
            }
        } else {
            if (stageDto.getMediaType().equals(MediaTypeEnum.VIDEO.getCode())) {
                cacheAudioText.setVisibility(View.VISIBLE);
                videoDivider.setVisibility(View.VISIBLE);
            } else {
                cacheVideoText.setVisibility(View.GONE);
                videoDivider.setVisibility(View.GONE);
            }
            cacheAudioText.setVisibility(View.GONE);
            audioDivider.setVisibility(View.GONE);
        }
        dialogPlus.show();
    }

    public void commentPopup(final ActionCommentDto actionCommentDto) {
        DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.popup_comment))
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.text_reply:
                                Intent intent = new Intent(StageDetailActivity.this, SubCommentsActivity.class);
                                intent.putExtra("comment", actionCommentDto);
                                startActivity(intent);
                                dialog.dismiss();
                                break;
                            case R.id.text_copy:
                                CommonUtils.copy(actionCommentDto.getContent(), StageDetailActivity.this);
                                Toast.makeText(StageDetailActivity.this, R.string.copy, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                break;
                            case R.id.text_cancel:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .create();
        dialogPlus.show();
    }
}
