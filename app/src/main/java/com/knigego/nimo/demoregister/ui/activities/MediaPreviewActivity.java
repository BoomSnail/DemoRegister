package com.knigego.nimo.demoregister.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;

public class MediaPreviewActivity extends BaseActivity {

    /**返回类型*/
    public static final String TYPE = "type";
    public static final String PATH = "path";

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_media_preview);
    }
}
