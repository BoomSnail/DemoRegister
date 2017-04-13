package com.knigego.nimo.demoregister.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.adapters.FolderAdapter;
import com.knigego.nimo.demoregister.ui.adapters.ImageGridAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.nereo.multi_image_selector.bean.Folder;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.utils.FileUtils;
import me.nereo.multi_image_selector.utils.ScreenUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MultiImageSelectFragment extends Fragment {

    /*** 最大图片选择次数，int类型*/
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /*** 图片选择模式，int类型*/
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /*** 是否显示相机，boolean类型*/
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /*** 默认选择的数据集*/
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";
    /*** 单选*/
    public static final int MODE_SINGLE = 0;
    /*** 多选*/
    public static final int MODE_MULTI = 1;

    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    private static final String KEY_TEMP_FILE = "key_temp_file";
    //请求加载系统相机
    private static final int REQUEST_CAMERA = 100;
    @Bind(R.id.grid)
    GridView mGrid;
    @Bind(R.id.category_btn)
    Button mCategoryBtn;
    @Bind(R.id.footer)
    RelativeLayout mFooter;

    //文件夹数据
    private ArrayList<Folder> mResultFolder = new ArrayList<>();
    private ArrayList<String> resultList;//结果图片

    private Callback mCallback;
    private ImageGridAdapter mImageGridAdapter;

    private FolderAdapter mFolderAdapter;
    private ListPopupWindow mFolderPopupWindow;
    private int mDesireImageCount;//数量
    private boolean mIsshowCamera;//是否显示相机


    private File mTmpFile;

    public MultiImageSelectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback interface...");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mis_fragment_multi_image, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    //获取activity传过来的参数
    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //选择图片数量
        mDesireImageCount = getArguments().getInt(EXTRA_SELECT_COUNT);

        final int mode = getArguments().getInt(EXTRA_SELECT_MODE);
        if (mode == MODE_MULTI) {
            ArrayList<String> tmp = getArguments().getStringArrayList(EXTRA_DEFAULT_SELECTED_LIST);
            if (tmp != null && tmp.size() > 0) {
                resultList = tmp;
            }
        }

        //是否显示相机
        mIsshowCamera = getArguments().getBoolean(EXTRA_SHOW_CAMERA);
        mImageGridAdapter = new ImageGridAdapter(getActivity(), mIsshowCamera, 3);
        //是否显示选择指示器
        mImageGridAdapter.showSelectIndicator(mode == MODE_MULTI);

        mCategoryBtn.setText(R.string.mis_folder_all);
        mCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                }
                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });

//        if (resultList == null && resultList.size()<=0) {
//        mPreview.setText(R.string.mis_preview);
//            mPreviewer.setEnabled(false);
//        }

//        mPreviewer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: 2017/4/10 //预览
//            }
//        });
        mGrid.setAdapter(mImageGridAdapter);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mImageGridAdapter.isShowCamera()) {
                    if (position == 0) {
                        if (mDesireImageCount == resultList.size()) {
                            Toast.makeText(getActivity(), R.string.mis_msg_amount_limit, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        showCameraAction();
                    } else {
                        Image image = (Image) parent.getAdapter().getItem(position);

                        selectImageFromGrid(image, mode);
                    }
                } else {
                    Image image = (Image) parent.getAdapter().getItem(position);
                    selectImageFromGrid(image, mode);
                }
            }
        });
        mGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_FLING) {
                    Glide.with(view.getContext()).pauseRequests();
                } else {
                    Glide.with(view.getContext()).resumeRequests();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mFolderAdapter = new FolderAdapter(getActivity());
    }

    //选择图片操作
    private void selectImageFromGrid(Image image, int mode) {
        if (image != null) {
            //多选模式
            if (mode == MODE_MULTI) {
                if (resultList.contains(image.path)) {
                    resultList.remove(image.path);//如果包含则移除
                    if (resultList.size() != 0) {

                    } else {

                    }

                    if (mCallback != null) {
                        mCallback.onImageUnselected(image.path);
                    }
                } else {
                    //判断选择数量问题
                    if (mDesireImageCount == resultList.size()) {
                        Toast.makeText(getActivity(), R.string.mis_msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    resultList.add(image.path);

                    if (mCallback != null) {
                        mCallback.onImageSelected(image.path);
                    }
                }
                mImageGridAdapter.select(image);
            }else if (mode == MODE_SINGLE) {
                //单选模式
                if (mCallback != null) {
                    mCallback.onSingleImageSelected(image.path);
                }
            }
        }
    }

    //选择相机
    private void showCameraAction() {
        //跳转至系统相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            try {
                mTmpFile = FileUtils.createTmpFile(getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mTmpFile != null && mTmpFile.exists()) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } else {
                Toast.makeText(getActivity(), "图片错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.mis_msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    //创建弹出的listView
    private void createPopupFolderList() {
        Point point = ScreenUtils.getScreenSize(getActivity());
        int width = point.x;
        int height = (int) (point.y * (4.5f / 8.0f));
        mFolderPopupWindow = new ListPopupWindow(getActivity());
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setHeight(height);
        mFolderPopupWindow.setAnchorView(mFooter);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                mFolderAdapter.setSelectIndex(position);
                final int index = position;
                final AdapterView adapterView = parent;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFolderPopupWindow.dismiss();
                        if (index == 0) {
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallbacks);
                            mCategoryBtn.setText(R.string.mis_folder_all);
                            if (mIsshowCamera) {
                                mImageGridAdapter.setShowCamera(true);
                            } else {
                                mImageGridAdapter.setShowCamera(false);
                            }
                        } else {
                            Folder folder = (Folder) adapterView.getAdapter().getItem(position);
                            if (null != folder) {
                                mImageGridAdapter.setData(folder.images);
                                mCategoryBtn.setText(folder.name);

                                //设定默认选择
                                if (resultList != null && resultList.size() > 0) {
                                    mImageGridAdapter.setDefaultSelected(resultList);
                                }
                            }
                            mImageGridAdapter.setShowCamera(false);
                        }
                        //滑动到最初位置
                        mGrid.smoothScrollToPosition(0);
                    }
                }, 100);
            }
        });

    }

    private boolean hasFolderGened = false;
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ALL) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[3] + "=? OR " + IMAGE_PROJECTION[3] + "=? ",
                        new String[]{"image/jpeg", "image/png"}, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            } else if (id == LOADER_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'",
                        null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }
            return null;

        }

        private boolean fileExist(String path) {
            if (!TextUtils.isEmpty(path)) {
                return new File(path).exists();
            }
            return false;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                if (data.getCount() > 0) {
                    List<Image> images = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndex(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndex(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndex(IMAGE_PROJECTION[2]));
                        Image image = null;
                        if (fileExist(path)) {
                            image = new Image(path, name, dateTime);
                            images.add(image);
                        }

                        if (!hasFolderGened) {
                            //获取文件夹名称
                            File folderFile = new File(path).getParentFile();
                            if (folderFile != null && folderFile.exists()) {
                                String fp = folderFile.getAbsolutePath();
                                Folder f = getFolderBtPath(fp);
                                if (f == null) {
                                    Folder folder = new Folder();
                                    folder.name = folderFile.getName();
                                    folder.path = fp;
                                    folder.cover = image;
                                    List<Image> imageList = new ArrayList<>();
                                    imageList.add(image);
                                    folder.images = imageList;
                                    mResultFolder.add(folder);
                                } else {
                                    f.images.add(image);
                                }
                            }
                        }
                    } while (data.moveToNext());

                    mImageGridAdapter.setData(images);
                    //设定默认选择
                    if (resultList != null && resultList.size() > 0) {
                        mImageGridAdapter.setDefaultSelected(resultList);
                    }

                    if (!hasFolderGened) {
                        mFolderAdapter.setData(mResultFolder);
                        hasFolderGened = true;
                    }
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private Folder getFolderBtPath(String fp) {
        if (mResultFolder != null) {
            for (Folder folder : mResultFolder) {
                if (TextUtils.equals(folder.path, fp)) {
                    return folder;
                }
            }
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_TEMP_FILE, mTmpFile);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mTmpFile = (File) savedInstanceState.getSerializable(KEY_TEMP_FILE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //首次加载所有图片
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallbacks);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //相机拍完照后，返回图片路径
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    if (mCallback != null) {
                        mCallback.onCameraShot(mTmpFile);
                    }
                } else {
                    while (mTmpFile != null && mTmpFile.exists()) {
                        boolean success = mTmpFile.delete();
                        if (success) {
                            mTmpFile = null;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mFolderPopupWindow != null) {
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            }
        }
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 回调接口
     */
    public interface Callback {
        void onSingleImageSelected(String path);

        void onImageSelected(String path);

        void onImageUnselected(String path);

        void onCameraShot(File imageFile);
    }

}
