package com.knigego.nimo.demoregister.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.base.BaseFragment;

/**
 * 聊天
 * Created by ThinkPad on 2017/3/28.
 */

public class ChatFragment extends BaseFragment {

    public static final String TAG = ChatFragment.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }


    @Override
    protected void onCreateView(View view, ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView: " + "onCreateView is on" );
    }

    @Override
    public void onStart() {
        Log.i(TAG ,"onStart");
        super.onStart();

    }

    @Override
    public void onResume() {
        Log.i(TAG,"onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause:");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
