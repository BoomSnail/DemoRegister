package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.ui.fragment.MultiImageSelectFragment;
import com.knigego.nimo.demoregister.util.QiNiuUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MultiImageSelectorActivity extends BaseActivity implements MultiImageSelectFragment.Callback {

    /*** 是否显示相机，默认显示*/
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /*** 最大图片选择次数，int类型，默认9*/
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /*** 图片选择模式，默认多选*/
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /*** 多选*/
    public static final int MODE_MULTI = 1;
    /*** 默认选择集*/
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    /*** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合*/

    public static final String EXTRA_RESULT = "extra_result";
    @Bind(R.id.image_grid)
    FrameLayout mImageGrid;

    private int mDefaultCount;
    private ArrayList<String> resultList = new ArrayList<>();
    private MenuItem mMenuItem;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_muliti_image_selector);
        ButterKnife.bind(this);
        setTitle(R.string.title_image);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT,9);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE,MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA,true);
        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectFragment.EXTRA_SELECT_COUNT,mDefaultCount);
        bundle.putInt(MultiImageSelectFragment.EXTRA_SELECT_MODE,mode);
        bundle.putBoolean(MultiImageSelectFragment.EXTRA_SHOW_CAMERA,isShow);
        bundle.putStringArrayList(MultiImageSelectFragment.EXTRA_DEFAULT_SELECTED_LIST,resultList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this,MultiImageSelectFragment.class.getName(),bundle))
                .commit();
    }

    @Override
    public boolean backEvent() {
        setResult(RESULT_CANCELED);
        return super.backEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_finish,menu);
        mMenuItem = menu.getItem(0);

        if (resultList == null && resultList.size() <= 0) {
            mMenuItem.setTitle(R.string.menu_finish);
            mMenuItem.setEnabled(false);
        } else {
            updateDoneText();
            mMenuItem.setEnabled(true);
        }
        return true;
    }

    //完成（1/9）
    private void updateDoneText() {
        mMenuItem.setTitle(String.format("%s(%d/%d)",getString(R.string.menu_finish),resultList.size(),mDefaultCount));
    }

    @Override
    protected boolean _onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  R.id.action_finish:
                if (resultList != null && resultList.size() >0) {
                    Intent data = new Intent();
                    data.putStringArrayListExtra(EXTRA_RESULT,resultList);//onActivityResult接受
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent intent = new Intent();
        resultList.add(path);
        intent.putStringArrayListExtra(EXTRA_RESULT,resultList);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if(!resultList.contains(path)) {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if(resultList.size() > 0){
            updateDoneText();
            if(!mMenuItem.isEnabled()){
                mMenuItem.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if(resultList.contains(path)){
            resultList.remove(path);
        }
        updateDoneText();
        // 当为选择图片时候的状态
        if(resultList.size() == 0){
            mMenuItem.setTitle(R.string.menu_finish);
            mMenuItem.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        if(imageFile != null) {

            // notify system
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
