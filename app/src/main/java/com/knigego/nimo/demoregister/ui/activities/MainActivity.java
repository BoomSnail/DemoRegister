package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.crooods.common.protocol.ResponseT;
import com.crooods.wd.dto.UserProfileDto;
import com.crooods.wd.service.IUserProfileStubService;
import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.net.RetrofitUtil;
import com.knigego.nimo.demoregister.storage.AppPref;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;
import com.knigego.nimo.demoregister.ui.fragment.ChatFragment;
import com.knigego.nimo.demoregister.ui.fragment.MomentFragment;
import com.knigego.nimo.demoregister.ui.fragment.StageFragment;
import com.knigego.nimo.demoregister.ui.fragment.UserInfoFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends BaseActivity {

    @Bind(R.id.bottom_navigation)
    BottomNavigationBar mBottomNavigation;

    private Menu mMenu;
    public static final int REQUEST_GO_TO_LOGIN = 1;

    private static String currentFragmentTag;
    public static int currentId;

    private StageFragment mStageFragment;
    private ChatFragment mChatFragment;
    private MomentFragment mMomentFragment;
    private UserInfoFragment mUserInfoFragment;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("肚皮舞");
        initView();
        if (!TextUtils.isEmpty(AppPref.getInstance().getAccessToken() )&& AppPref.getInstance().getUserProfile() == null) {
            loadUserInfo();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("current",currentId);
        outState.putString("currentFragmentTag",currentFragmentTag);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        currentId = savedInstanceState.getInt("currentId",currentId);
        currentFragmentTag = savedInstanceState.getString("currentFragmentTag",currentFragmentTag);
        viewSelect(currentId);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        viewSelect(0);
        return true;
    }

    @Override
    public boolean showHomeAsUpEnabled() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_all_dancer:
                startActivity(new Intent(this,AllDancerActivity.class));
                break;
            case R.id.action_stage_release:
                startActivity(new Intent(this,ReleaseChooseActivity.class));
                break;
            case R.id.action_add_friend:

                break;
            case R.id.action_my_friends:

                break;
            case R.id.action_moment_release:
                startActivity(new Intent(this,ReleaseMomentActivity.class));
                break;
            case R.id.action_setting:

                startActivity(new Intent(this,SettingActivity.class));
                break;
            default:
                break;
        }

        return true;
    }

    //
    private void initView() {
        mBottomNavigation.setMode(BottomNavigationBar.MODE_SHIFTING);
        //添加选项
        mBottomNavigation.addItem(new BottomNavigationItem(R.drawable.main_tab_dance_selector,"舞台"))
                .addItem(new BottomNavigationItem(R.drawable.main_tab_chat_selector,"交友"))
                .addItem(new BottomNavigationItem(R.drawable.main_tab_find_selector,"动态"))
                .addItem(new BottomNavigationItem(R.drawable.main_tab_me_selector,"我的"))
                .initialise();

        mBottomNavigation.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {

                switch (position) {
                    case 0:
                        mToolbar.setBackgroundResource(R.color.white);
                        mToolbar.setTitleTextColor(getResources().getColor(R.color.actionbar_text_color));
                        setTitle("肚皮舞");

                        if (mMenu != null) {
                            mMenu.getItem(0).setVisible(true);
                            mMenu.getItem(1).setVisible(true);
                            mMenu.getItem(2).setVisible(false);
                            mMenu.getItem(3).setVisible(false);
                            mMenu.getItem(4).setVisible(false);
                            mMenu.getItem(5).setVisible(false);
                        }
                        viewSelect(0);
                        break;
                    case 1:
                        mToolbar.setBackgroundResource(R.color.white);
                        mToolbar.setTitleTextColor(getResources().getColor(R.color.actionbar_text_color));
                        setTitle("肚皮舞");
                        if (mMenu != null) {
                            mMenu.getItem(0).setVisible(false);
                            mMenu.getItem(1).setVisible(false);
                            mMenu.getItem(2).setVisible(true);
                            mMenu.getItem(3).setVisible(true);
                            mMenu.getItem(4).setVisible(false);
                            mMenu.getItem(5).setVisible(false);

                        }
                        viewSelect(1);
                        break;
                    case 2:
                        mToolbar.setBackgroundResource(R.color.white);
                        mToolbar.setTitleTextColor(getResources().getColor(R.color.actionbar_text_color));

                        setTitle("动态");
                        if (mMenu != null) {
                            mMenu.getItem(0).setVisible(false);
                            mMenu.getItem(1).setVisible(false);
                            mMenu.getItem(2).setVisible(false);
                            mMenu.getItem(3).setVisible(false);
                            mMenu.getItem(4).setVisible(true);
                            mMenu.getItem(5).setVisible(false);
                        }
                        viewSelect(2);

                        break;
                    case 3:
                        if (TextUtils.isEmpty(AppPref.getInstance().getAccessToken())) {
                            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                            intent.putExtra("fromMain",true);
                            startActivityForResult(intent,REQUEST_GO_TO_LOGIN);
                            return;
                        }
                        mToolbar.setBackgroundResource(R.color.colorAccent);
                        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

                        UserProfileDto userProfileDto = AppPref.getInstance().getUserProfile();
                        String nickName = "";
                        if (userProfileDto != null) {
                            nickName = userProfileDto.getUserBaseDto().getNickName();
                        }
                        if (!TextUtils.isEmpty(nickName)) {
                            setTitle(nickName);
                        }
                        if (mMenu != null) {
                            mMenu.getItem(0).setVisible(false);
                            mMenu.getItem(1).setVisible(false);
                            mMenu.getItem(2).setVisible(false);
                            mMenu.getItem(3).setVisible(false);
                            mMenu.getItem(4).setVisible(false);
                            mMenu.getItem(5).setVisible(true);
                        }
                        viewSelect(3);
                        break;
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

    }

    private void viewSelect(int i) {
        Fragment fragment = null;
        String tag = null;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        mStageFragment = (StageFragment) fm.findFragmentByTag(StageFragment.class.getSimpleName());
        mChatFragment = (ChatFragment) fm.findFragmentByTag(ChatFragment.class.getSimpleName());
        mMomentFragment = (MomentFragment) fm.findFragmentByTag(MomentFragment.class.getSimpleName());
        mUserInfoFragment = (UserInfoFragment) fm.findFragmentByTag(UserInfoFragment.class.getSimpleName());
        hideFragment(ft);

        switch(i){
            case 0:
                if (mMenu != null) {
                    mMenu.getItem(0).setVisible(true);
                    mMenu.getItem(1).setVisible(true);
                    mMenu.getItem(2).setVisible(false);
                    mMenu.getItem(3).setVisible(false);
                    mMenu.getItem(4).setVisible(false);
                    mMenu.getItem(5).setVisible(false);
                }
                tag = StageFragment.class.getSimpleName();
                Fragment home = fm.findFragmentByTag(tag);
                if (home != null) {
                    fragment = home;
                } else {
                    fragment = new StageFragment();
                }

                break;
            case 1:
                tag = ChatFragment.class.getSimpleName();
                Fragment chat = fm.findFragmentByTag(tag);
                if (chat != null) {
                    fragment = chat;
                } else {
                    fragment = new ChatFragment();
                }
                break;
            case 2:
                tag = MomentFragment.class.getSimpleName();
                Fragment moment = fm.findFragmentByTag(tag);
                if (moment != null) {
                    fragment = moment;
                } else {
                    fragment = new MomentFragment();
                }
                break;
            case 3:
                tag = UserInfoFragment.class.getSimpleName();
                Fragment userInfo = fm.findFragmentByTag(tag);
                if (userInfo != null) {
                    fragment = userInfo;
                } else {
                    fragment = new UserInfoFragment();
                }
                if (fragment instanceof UserInfoFragment) {
                    ((UserInfoFragment)fragment).notifyDataSetChanged();
                }
                break;
        }
        if (fragment != null && fragment.isAdded()) {
            ft.show(fragment);
        } else {
            ft.add(R.id.main_fragment,fragment,tag);
        }
        ft.commitAllowingStateLoss();
        currentFragmentTag = tag;
        currentId = i;
    }

    private void hideFragment(FragmentTransaction ft) {
        if (mStageFragment != null) {
            ft.hide(mStageFragment);
        }
        if (mChatFragment != null) {
            ft.hide(mChatFragment);
        }
        if (mMomentFragment != null) {
            ft.hide(mMomentFragment);
        }

        if (mUserInfoFragment != null) {
            ft.hide(mUserInfoFragment);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


    }

    private void loadUserInfo() {
        IUserProfileStubService iUserProfileStubService = RetrofitUtil.createApi(IUserProfileStubService.class);
        iUserProfileStubService.getUserProfile(AppPref.getInstance().getAccessToken(),
                new RetrofitUtil.ActivityCallback<ResponseT<UserProfileDto>>(this) {
                    @Override
                    public void success(ResponseT<UserProfileDto> userProfileDtoResponseT, Response response) {
                        super.success(userProfileDtoResponseT, response);
                        if (userProfileDtoResponseT.getRtnCode().equals(AppConstants.SUCCESS)) {
                            UserProfileDto userProfileDto = userProfileDtoResponseT.getBizData();
                            AppPref.getInstance().saveUserProfile(userProfileDto);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateUserTitle(Object p0) {

    }


//
//    private void loadUserAvator(UserProfileDto userProfileDto) {
//        Glide.with(this).load(userProfileDto.getUserBaseDto().getAvator())
//                .centerCrop()
//                .transform(new GlideCircleTransform(this))
//                .into(mBigImageview);
//
//        Glide.with(this)
//                .load(userProfileDto.getUserBaseDto().getAvator())
//                .centerCrop()
//                .transform(new GlideCircleTransform(this))
//                .into(mSmallImageView);
//    }
}
