package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.crooods.common.protocol.ResponseT;
import com.crooods.common.protocol.dto.StringWrapper;
import com.crooods.wd.service.IUserProfileStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.util.AndroidBug5497Workaround;
import com.knigego.nimo.demoregister.util.CommonUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserAddressEditActivity extends BaseActivity {

    @Bind(R.id.text_address)
    TextView mTextAddress;
    @Bind(R.id.edit_address)
    EditText mEditAddress;
    @Bind(R.id.text_edit_address)
    TextView mTextEditAddress;
    @Bind(R.id.layout_address)
    LinearLayout mLayoutAddress;
    @Bind(R.id.layout_address_tip)
    LinearLayout mLayoutAddressTip;
    private boolean isLocationSuccess;

    private MyLocationListener mListener;
    private LocationClient mClient;
    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_address_edit);
        AndroidBug5497Workaround.assistActivity(this);
        ButterKnife.bind(this);
        setTitle(R.string.title_edit_address);
        initLocation();
    }

    private void initLocation() {
        mListener = new MyLocationListener();
        mClient = new LocationClient(getApplicationContext());
        mClient.registerLocationListener(mListener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        int span = 0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(false);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(false);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mClient.setLocOption(option);
        mClient.start();

        mTextAddress.setText(R.string.address_location);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mClient.unRegisterLocationListener(mListener);
        }

        mClient.stop();
        mClient= null;
    }

    @OnClick(R.id.text_edit_address)
    public void editAddressAction(){
        mTextAddress.setVisibility(View.GONE);
        mEditAddress.setVisibility(View.VISIBLE);
        mTextEditAddress.setVisibility(View.GONE);
        mLayoutAddress.setVisibility(View.VISIBLE);
        mLayoutAddressTip.setVisibility(View.VISIBLE);
        mEditAddress.requestFocus();
        CommonUtils.showSoftInputMethod(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_userinfo_edit,menu);
        return true;
    }

    @Override
    protected boolean _onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                saveAddress();
                break;
            default:
                break;
        }
        return true;
    }

    private void saveAddress() {
        if (mEditAddress.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(mEditAddress.getText().toString().trim())) {
                Toast.makeText(this, R.string.address_empty, Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra("address", mEditAddress.getText().toString().trim());
                setResult(RESULT_OK);
                finish();
            }
        } else {

            if (!isLocationSuccess) {
                if (mTextAddress.getText().toString().equals(getString(R.string.address_location_fail))) {
                    Toast.makeText(this, R.string.address_location_fail, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.address_location_tip, Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent();
                intent.putExtra("address",mTextAddress.getText().toString().trim());
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();

            getAddress(latitude,longitude);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
    private void getAddress(double latitude, double longitude) {
        IUserProfileStubService stubService = createApi(IUserProfileStubService.class);
        stubService.getAddress(AppPref.getInstance().getAccessToken(),longitude,latitude ,
                new RetrofitUtil.ActivityCallback<ResponseT<StringWrapper>>(this) {
                    @Override
                    public void success(ResponseT<StringWrapper> stringWrapperResponseT, Response response) {
                        super.success(stringWrapperResponseT, response);
                        if (stringWrapperResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {

                            isLocationSuccess = true;
                            mTextAddress.setText(stringWrapperResponseT.getBizData().getS());
                            Log.i("-------", "success: "  + stringWrapperResponseT.getBizData().getS());
                        } else {
                            mTextAddress.setText(R.string.address_location_fail);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        mTextAddress.setText(R.string.address_location_fail);
                    }
                });
    }
}
