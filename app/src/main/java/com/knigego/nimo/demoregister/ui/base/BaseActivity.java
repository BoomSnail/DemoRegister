package com.knigego.nimo.demoregister.ui.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.util.ActivityManagers;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 *
 * Created by ThinkPad on 2017/3/27.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    protected View mViewDivider;
    protected ActionBar mActionBar;
    protected LinearLayout mRootView;
    protected ProgressDialog mProgressDialog;
    protected SystemBarTintManager mSystemBarTintManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_base);
        ActivityManagers.getManager().addActivity(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            mSystemBarTintManager = new SystemBarTintManager(this);
            mSystemBarTintManager.setStatusBarTintColor(getResources().getColor(R.color.colorAccent));
            mSystemBarTintManager.setStatusBarTintEnabled(true);
            mSystemBarTintManager.setNavigationBarTintColor(getResources().getColor(R.color.black));
            mSystemBarTintManager.setNavigationBarTintEnabled(true);
        }
        initToolbar();
        _onCreate(savedInstanceState);


    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewDivider = findViewById(R.id.view_divider);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mActionBar = getSupportActionBar();
            if (mActionBar != null) {
                mActionBar.setDisplayShowHomeEnabled(true);
                if (showHomeAsUpEnabled()) {
                    mActionBar.setHomeButtonEnabled(false);
                    mActionBar.setDisplayHomeAsUpEnabled(true);
                    mActionBar.setHomeAsUpIndicator(R.drawable.actionbar_up_selector);
                }
            }
        }
    }

    public void setTitle(String title){
        if (mActionBar != null) {
            mActionBar.setTitle(title);
        }
    }

    public void setTitle(int resId){
        if (mActionBar != null) {
            mActionBar.setTitle(resId);
        }
    }

    /**
     * 是否设置标题栏返回
     * @return
     */
    public boolean showHomeAsUpEnabled() {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (backEvent()) {
            super.onBackPressed();
        }
    }

    public boolean backEvent() {

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManagers.getManager().finishActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (backEvent()) {
                    this.finish();
                }
                break;
            default:
                break;

        }
        return _onOptionsItemSelected(item);
    }

    protected  boolean _onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    public void hideToolbar(boolean statusBarTintEnable){
        if (mToolbar != null) {
            mToolbar .setVisibility(View.GONE);
            mViewDivider.setVisibility(View.GONE);
            mActionBar.hide();
        }
        if (mSystemBarTintManager != null) {
            mSystemBarTintManager.setStatusBarTintEnabled(statusBarTintEnable);
        }
    }

    public void setContentView(int layoutId){
        setContentView(View.inflate(this,layoutId,null));
    }

    @Override
    public void setContentView(View view) {
        mRootView = (LinearLayout) findViewById(R.id.root_layout);
        if (mRootView == null) {
            return;
        }
        mRootView.addView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initToolbar();
    }

    public <T> T createApi(Class<T> clz){
        return RetrofitUtil.createApi(clz);
    }

    public ProgressDialog showProgress(int msgId) {
        return showProgress("", getString(msgId), -1);
    }

    public ProgressDialog showProgress(String message) {
        return showProgress("", message, -1);
    }

    public ProgressDialog showProgress(String title, String message) {
        return showProgress(title, message, -1);
    }

    public ProgressDialog showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onDialogCancelEvent(dialog);
                }
            });
        }

        if (!TextUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
        return mProgressDialog;
    }

    public void hideProgress(){
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void onDialogCancelEvent(DialogInterface dialog) {
        dialog.dismiss();
    }

    protected abstract void _onCreate(Bundle savedInstanceState);
}
