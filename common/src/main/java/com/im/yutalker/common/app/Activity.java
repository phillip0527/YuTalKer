package com.im.yutalker.common.app;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Window;

import com.im.yutalker.common.R;
import com.im.yutalker.common.widget.convention.PlaceHolderView;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Phillip on 2017/12/23.
 */

public abstract class Activity extends AppCompatActivity {
    protected PlaceHolderView placeHolderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在界面未初始化之前调用的初始化窗口
        initWidows();
        if (initArgs(getIntent().getExtras())) {
            // 得到界面id并设置到activity界面中
            int layId = getContentLayoutId();
            setContentView(layId);
            initBefore();
            initWidget();
            initData();
        } else {
//            finishAfterTransition();
            finish();
        }

    }

    /**
     * 初始化控件调用之前
     */
    protected void initBefore(){

    }

    /**
     * 初始化窗口
     */
    protected void initWidows() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {
            }
        }
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//        //开启转场动画
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setEnterTransition(new Fade().setDuration(500));
//        getWindow().setExitTransition(new Fade().setDuration(500));
    }

    /**
     * 初始化相关参数
     *
     * @param bundle 参数Bundle
     * @return 如果参数正确返回True，错误返回false
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /**
     * 得到当前界面的资源文件Id
     *
     * @return 资源文件Id
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    // 设置占位布局
    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
        this.placeHolderView = placeHolderView;
    }

    @Override
    public boolean onSupportNavigateUp() {
        // 当点击界面导航返回时，finish当前界面
//        finishAfterTransition();
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // 得到当前activity下的所有fragment
        List<android.support.v4.app.Fragment> fragments = getSupportFragmentManager().getFragments();
        // 判断是否为空
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                // 判断是否为我们能够处理的fragment类型
                if (fragment instanceof com.im.yutalker.common.app.Fragment) {
                    // 判断是否拦截了返回按钮
                    if (((com.im.yutalker.common.app.Fragment) fragment).onBackPressed()) {
                        // 如果有直接return
                        return;
                    }
                }
            }
        }

        super.onBackPressed();
//        finishAfterTransition();
        finish();
    }
}
