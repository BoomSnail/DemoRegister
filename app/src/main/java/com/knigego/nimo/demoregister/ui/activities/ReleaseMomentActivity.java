package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.crooods.common.protocol.ResponseT;
import com.crooods.wd.dto.post.MediaImageDto;
import com.crooods.wd.dto.post.MomentDto;
import com.crooods.wd.dto.post.MomentImageCreateDto;
import com.crooods.wd.dto.post.MomentVideoCreateDto;
import com.crooods.wd.enums.ClientTypeEnum;
import com.crooods.wd.enums.MediaTypeEnum;
import com.crooods.wd.service.IMomentStubService;
import com.knigego.nimo.demoregister.AppConfig;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.adapters.MomentMediaAdapter;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.ui.model.MomentItem;
import com.knigego.nimo.demoregister.ui.model.MomentMediaItem;
import com.knigego.nimo.demoregister.util.AndroidBug5497Workaround;
import com.knigego.nimo.demoregister.util.BitmapUtil;
import com.knigego.nimo.demoregister.util.BrodcastHelper;
import com.knigego.nimo.demoregister.util.DeviceUtils;
import com.knigego.nimo.demoregister.util.QiNiuUtils;
import com.knigego.nimo.demoregister.util.UIHelper;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.vov.vitamio.provider.MediaStore;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReleaseMomentActivity extends BaseActivity {

    private static final String TAG = ReleaseMomentActivity.class.getSimpleName();
    private static final int VIDEO_REQUEST_CODE = 2;
    private static final int IMAGE_REQUEST_CODE = 1;

    @Bind(R.id.edit_content)
    EditText mEditContent;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private MomentMediaAdapter mAdapter;

    private boolean isReleaseVideo = false;
    private String mVideoKey = "";
    private ArrayList<String> mSelectPath;
    private boolean cancelUpload = false;
    private final static int MSG_UPLOAD_COMPLETE = 1;
    private static final int MSG_UPLOADING = 0;


    private boolean isUploadVideoComplete;
    private boolean isUploadCoverComplete;
    private String mVideoPath;
    private String mVideoLocalPath;
    private String mVideoLocalImagePath;
    private String mCoverPath;


    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_release_moment);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);
        setTitle(R.string.title_release_moment);
        init();
    }

    private void init() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new spaceItemDecoration(DeviceUtils.dipToPX(this, 10)));
        mAdapter = new MomentMediaAdapter(this, new ArrayList<MomentMediaItem>());
        mRecyclerView.setAdapter(mAdapter);
        updateMomentMediaHeight();
    }

    private void updateMomentMediaHeight() {
        if (mAdapter != null) {
            int imageCount = mAdapter.getItemCount();
            ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
            int itemHeight = DeviceUtils.dipToPX(this, 85);
            int dividerHeight = DeviceUtils.dipToPX(this, 10);
            params.height = (imageCount / 3 + (imageCount < 9 ? 1 : 0)) * itemHeight//item总高度
                    + (imageCount / 3 - (imageCount < 9 ? 0 : 1)) * dividerHeight;//刚好九个时不计算底部高度
            params.width = DeviceUtils.dipToPX(this, 90) * 3 + dividerHeight * 3;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_finish, menu);
        return true;
    }

    @Override
    protected boolean _onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_finish:
                if (isReleaseVideo) {
                    releaseVideo();
                } else {
                    releaseImages();
                }
                break;
            default:
                break;
        }
        return true;
    }

    //上传到七牛，同时发布动态
    private void releaseImages() {
        if (mAdapter != null) {
            if (mAdapter.getMediaItems().size() == 0 &&
                    TextUtils.isEmpty(mEditContent.getText().toString().trim())) {
                Toast.makeText(this, R.string.moment_content_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            cancelUpload = false;
            showProgress(R.string.loading_release);
            List<MomentMediaItem> mediaItems = mAdapter.getMediaItems();
            if (mediaItems.size() > 0) {
                for (int i = 0; i < mediaItems.size(); i++) {
                    MomentMediaItem momentMediaItem = mediaItems.get(i);
                    if (momentMediaItem.getMediaType() == MomentMediaItem.MEDIA_TYPE_IMAGE) {
                        momentMediaItem.setUpLoading(true);
                        uploadImage(momentMediaItem.getKey(), momentMediaItem.getFilePath());
                    }
                }
            } else {
                createMomentImages();
            }
        }
    }

    //发布的动态显示到动态
    private void createMomentImages() {
        if (mAdapter != null) {
            List<MomentMediaItem> mediaItems = mAdapter.getMediaItems();//获取发布的图片item总数

            IMomentStubService stubService = createApi(IMomentStubService.class);

            MomentImageCreateDto createDto = new MomentImageCreateDto();
            createDto.setContent(mEditContent.getText().toString().trim());
            createDto.setIp("");
            createDto.setMediaType(MediaTypeEnum.IMAGE.getCode());
            createDto.setDevice(ClientTypeEnum.ANDROID.getCode());
            createDto.setUserid(AppPref.getInstance().getUserId());

            //设置图片
            List<MediaImageDto> mediaImageDtos = new ArrayList<>();
            for (int i = 0; i < mediaItems.size(); i++) {
                MomentMediaItem mediaItem = mediaItems.get(i);
                MediaImageDto mediaImageDto = new MediaImageDto();
                mediaImageDto.setUrl(mediaItem.getImageUrl());
                mediaImageDto.setSize(0);
                mediaImageDto.setSort(i);
                mediaImageDtos.add(mediaImageDto);
            }

            createDto.setImages(mediaImageDtos);

            //发布动态后发送广播提醒动态页更新、我的发布的动态更新
            stubService.imageCreate(AppPref.getInstance().getAccessToken(), createDto,
                    new RetrofitUtil.ActivityCallback<ResponseT<MomentDto>>(this) {
                        @Override
                        public void success(ResponseT<MomentDto> momentDtoResponseT, Response response) {
                            super.success(momentDtoResponseT, response);
                            hideProgress();
                            if (momentDtoResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                                BrodcastHelper.sendMomentUpdateAction(ReleaseMomentActivity.this);
                                BrodcastHelper.sendUserInfoUpdateAction(ReleaseMomentActivity.this);
                                finish();
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


    private void createMomentVideo() {
        IMomentStubService stubService = createApi(IMomentStubService.class);
        MomentVideoCreateDto createDto = new MomentVideoCreateDto();

        createDto.setSize(0);
        createDto.setIp("");
        createDto.setMediaType(MediaTypeEnum.VIDEO.getCode());
        createDto.setDevice(ClientTypeEnum.ANDROID.getCode());
        createDto.setUserid(AppPref.getInstance().getUserId());
        createDto.setCoverUrl(mCoverPath);
        createDto.setLength(0);
        createDto.setUrl(mVideoPath);
        createDto.setVideoKey(mVideoKey);
        createDto.setContent(mEditContent.getText().toString().trim());

        stubService.videoCreate(AppPref.getInstance().getAccessToken(),createDto,
                new RetrofitUtil.ActivityCallback<ResponseT<MomentDto>>(this){
                    @Override
                    public void success(ResponseT<MomentDto> momentDtoResponseT, Response response) {
                        super.success(momentDtoResponseT, response);
                        hideProgress();
                        if (momentDtoResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            BrodcastHelper.sendMomentUpdateAction(ReleaseMomentActivity.this);
                            finish();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        hideProgress();
                    }
                });
    }

    //上传图片到七牛
    private void uploadImage(String key, String filePath) {
        QiNiuUtils.upLoadImages(this, "moments", MediaTypeEnum.IMAGE.getCode(), filePath, key, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {//上传完成
                    String imagePath = "http://" + AppConfig.QINIU_DOMAIN + "/" + key;
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_UPLOAD_COMPLETE;
                    Bundle bundle = new Bundle();
                    bundle.putString("key", key);
                    bundle.putString("imagePath", imagePath);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            }
        }, new QiNiuUtils.CancelCallBack() {
            @Override
            public boolean isCancelled(String key) {
                return cancelUpload;
            }
        }, null);
    }

    //上传视频到七牛，并发布到动态
    private void releaseVideo() {
        cancelUpload = false;
        showProgress(R.string.loading_release);
        uploadVideo();
        uploadVideoCover();
    }


    private void uploadVideo() {
        QiNiuUtils.upLoadVideo(this, "moments", MediaTypeEnum.IMAGE.getCode(), mVideoLocalPath,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        try {

                            if (!info.isOK()) {
                                isUploadVideoComplete = false;
                                mVideoPath = "";
                                mVideoKey = "";
                                Toast.makeText(ReleaseMomentActivity.this, R.string.upload_video_fail, Toast.LENGTH_SHORT).show();
                            } else {
                                mVideoPath = "http://" + AppConfig.QINIU_DOMAIN + "/" + key;
                                mVideoKey = key;
                                isUploadVideoComplete = true;
                                mHandler.obtainMessage(MSG_UPLOAD_COMPLETE, 100).sendToTarget();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            isUploadVideoComplete = false;
                            mVideoPath = "";
                            mVideoKey = "";
                        }
                    }
                }, new UpCancellationSignal() {
                    @Override
                    public boolean isCancelled() {
                        return cancelUpload;
                    }
                }, null);
    }

    private void uploadVideoCover() {
        QiNiuUtils.upLoad(this, "moments", MediaTypeEnum.IMAGE.getCode(), mVideoLocalImagePath,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        try {
                            if (info.isOK()) {
                                isUploadCoverComplete = true;
                                mCoverPath = "http://" + AppConfig.QINIU_DOMAIN + "/" + key;
                                mHandler.obtainMessage(MSG_UPLOAD_COMPLETE, 100).sendToTarget();
                            } else {
                                isUploadCoverComplete = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            isUploadCoverComplete = false;
                            mCoverPath = "";
                        }
                    }
                }, new UpCancellationSignal() {
                    @Override
                    public boolean isCancelled() {
                        return cancelUpload;
                    }
                });
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPLOADING:
                    break;
                case MSG_UPLOAD_COMPLETE:
                    if (isReleaseVideo) {
                        if (isUploadVideoComplete && isUploadCoverComplete) {
                            createMomentVideo();
                        }
                    } else {
                        Bundle bundle = msg.getData();
                        String key = bundle.getString("key");
                        String imagePath = bundle.getString("imagePath");
                        if (mAdapter != null) {
                            mAdapter.setItemUploadComplete(key, imagePath);
                            if (mAdapter.isAllImageUploadComplete()) {
                                createMomentImages();
                            }
                        }

                    }

            }
            super.handleMessage(msg);
        }
    };


    //打开图库
    public void openImageAlbum() {
        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        //是否显示调用相机拍照
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        //最大图片显示数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        //设置模式(支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        //默认选择图片，回填选项(支持String ArrayList)
        //默认选择
        //如果第一次选择有图片，第二次又去选择，则把之前选中的传进去
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }

        startActivityForResult(intent, IMAGE_REQUEST_CODE);

    }

    //picture viewr activity
    public void openPictureView(int position) {
        String[] images = getPictures();
        if (images == null && images.length == 0) {
            return;
        }

        Intent intent = new Intent(this, PictureViewActivity.class);
        intent.putExtra(PictureViewActivity.PICTURES, images);
        intent.putExtra(PictureViewActivity.POSITION, position);
        startActivity(intent);
    }

    private String[] getPictures() {
        if (mSelectPath != null) {
            String[] images = new String[mSelectPath.size()];
            for (int i = 0; i < images.length; i++) {
                images[i] = mSelectPath.get(i);
            }
            return images;
        }
        return null;
    }

    //视频预览
    public void openVideoView() {
        if (TextUtils.isEmpty(mVideoLocalPath)) {
            return;
        }
        UIHelper.goToVideoPlayActivity(this, mVideoLocalPath);
    }

    public void openVideoAlbum() {
        Intent intent = new Intent(this, MediaChooseActivity.class);
        intent.putExtra("maxDuration", 40 * 1000);
        startActivityForResult(intent, VIDEO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == VIDEO_REQUEST_CODE) {
                mVideoLocalPath = data.getStringExtra(MediaPreviewActivity.PATH);
                if (!TextUtils.isEmpty(mVideoLocalPath)) {
                    mVideoLocalImagePath = getVideoImagePath();
                }
                decodeThumbBitmapForFile(mVideoLocalPath,mVideoLocalImagePath);
                if (TextUtils.isEmpty(mVideoLocalImagePath)) {
                    return;
                }

                MomentMediaItem momentMediaItem =  new MomentMediaItem();
                momentMediaItem.setMediaType(MomentMediaItem.MEDIA_TYPE_VIDEO);
                momentMediaItem.setFilePath(mVideoLocalPath);
                momentMediaItem.setCoverPath(mVideoLocalImagePath);

                if (mAdapter != null) {
                    isReleaseVideo = true;
                    mAdapter.addMediaItem(momentMediaItem);
                    mAdapter.notifyDataSetChanged();
                    updateMomentMediaHeight();
                }
            } else if (requestCode == IMAGE_REQUEST_CODE) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (mSelectPath == null || mSelectPath.size() == 0) {
                    return;
                }

                List<MomentMediaItem> mediaItems = new ArrayList<>();
                for (int i = 0; i < mSelectPath.size(); i++) {
                    MomentMediaItem momentMediaItem = new MomentMediaItem();
                    momentMediaItem.setFilePath(mSelectPath.get(i));
                    momentMediaItem.setMediaType(MomentMediaItem.MEDIA_TYPE_IMAGE);
                    momentMediaItem.setKey(QiNiuUtils.getImageKey());
                    mediaItems.add(momentMediaItem);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (mAdapter != null) {
                    isReleaseVideo = false;
                    mAdapter.setData(mediaItems);
                }
                //根据madapter的item数量，设置recyclerView的高度
                updateMomentMediaHeight();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void decodeThumbBitmapForFile(String path, String imagePath) {
        File file = new File(imagePath);
        if (file.exists()) {
            return;
        }

        int screen = DeviceUtils.getScreenWidth(this);
        //还可以选择MINI_KIND和MICRO_KIND
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, android.provider.MediaStore.Video.Thumbnails.MINI_KIND);
        if (bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,screen,screen);
        }
        BitmapUtil.bitmapToFile(bitmap,imagePath);//生成图片

    }

    private String getVideoImagePath() {
        return new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath())).append(
                AppConfig.PATH_VIDEO_IMAGE).append(System.currentTimeMillis()).toString();
    }

    private class spaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public spaceItemDecoration(int i) {
            this.space = i;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            outRect.right = space;
            if (position > 3) {
                outRect.top = space;
            }
        }
    }
}
