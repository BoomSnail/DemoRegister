package com.knigego.nimo.demoregister.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.net.RetrofitUtil;

/**
 * Fragment register broadcastReceiver
 * Created by ThinkPad on 2017/3/27.
 */

public abstract class BaseFragment extends Fragment {

    protected UpdateReceiver mUpdateReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
    }

    private void registerReceiver() {

        mUpdateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.ACTION_STAGE_UPDATE);
        intentFilter.addAction(AppConstants.ACTION_MOMENT_UPDATE);
        intentFilter.addAction(AppConstants.ACTION_STAGE_DELETE);
        intentFilter.addAction(AppConstants.ACTION_MOMENT_DELETE);
        intentFilter.addAction(AppConstants.ACTION_USER_INFO_UPDATE);
        intentFilter.addAction(AppConstants.ACTION_CHAT_LIST_UPDATE);

        getActivity().registerReceiver(mUpdateReceiver,intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(),container,false);
        onCreateView(view,container,savedInstanceState);

        return view;
    }

    protected abstract int getLayoutId();
    protected abstract void onCreateView(View view,ViewGroup container,Bundle savedInstanceState);

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    private void unRegisterReceiver() {
        if (mUpdateReceiver != null) {
            getActivity().unregisterReceiver(mUpdateReceiver);
        }
    }

    protected  <T> T createApi(Class<T> tClass){
        return RetrofitUtil.createApi(tClass);
    }
    private class UpdateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            onUpdateReceiver(intent);
        }

    }
    protected void onUpdateReceiver(Intent intent) {
    }
}
