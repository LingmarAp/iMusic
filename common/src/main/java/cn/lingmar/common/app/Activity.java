package cn.lingmar.common.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;
import cn.lingmar.common.widget.convention.PlaceHolderView;

/**
 * Created by Lingmar on 2017/10/21.
 */

public abstract class Activity extends AppCompatActivity {
    protected PlaceHolderView mPlaceHolderView;

    protected static Activity currentActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initWindows();

        // 保存当前的Activity
        this.currentActivity = this;

        if (initArgs(getIntent().getExtras())) {
            // 得到界面Id并设置到Activity中
            int layId = getContentLayoutId();
            setContentView(layId);
            initBefore();
            initWidget();
            initData();
        } else {
            finish();
        }
    }

    protected void initBefore() {

    }

    protected void initWindows() {
    }

    /**
     * 初始化参数
     *
     * @param bundle
     * @return 参数正确返回True，否则返回False
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /**
     * 得到界面的资源文件Id
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

    @Override
    public boolean onSupportNavigateUp() {
        // 当点击界面导航返回时,finish
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        List<android.support.v4.app.Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (android.support.v4.app.Fragment fragment : fragments) {
                // 当这个Fragment是我们自己定义的
                if (fragment instanceof Fragment) {
                    if (((Fragment) fragment).onBackPressed() == true) {
                        return;
                    }
                }
            }
        }

        finish();
    }


    /**
     * 设置占位布局
     *
     * @param mPlaceHolderView 继承了占位布局规范的View
     */
    public void setPlaceHolderView(PlaceHolderView mPlaceHolderView) {
        this.mPlaceHolderView = mPlaceHolderView;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

}
