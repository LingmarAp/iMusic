package cn.lingmar.music;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Property;
import android.view.View;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;

import java.util.List;

import cn.lingmar.common.app.Activity;
import cn.lingmar.factory.Factory;
import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.data.helper.DbHelper;
import cn.lingmar.factory.model.Music;
import cn.lingmar.factory.service.MusicService;
import cn.lingmar.music.activities.MainActivity;
import cn.lingmar.music.frags.PermissionsFragment;

public class LaunchActivity extends Activity
        implements DbHelper.ChangedListener<Music> {
    private ColorDrawable mBgDrawable;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initBefore() {
        super.initBefore();
        // 注册数据库监听事件
        DbHelper.addChangedListener(Music.class, this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 拿到根布局
        View root = findViewById(R.id.activity_launch);
        // 获取颜色
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        ColorDrawable drawable = new ColorDrawable(color);

        root.setBackground(drawable);
        mBgDrawable = drawable;
    }

    @Override
    protected void initData() {
        super.initData();

        // 开始动画
        startAnim(0.3f, () -> waitScanFile());
    }

    /**
     * 等待扫描音乐文件
     */
    private void waitScanFile() {
        // 检测是否包含权限（后续如联网等）
        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
            // 扫描手机内音乐文件
            Factory.getMusicHelper().refresh(this, (DataSource.SucceedCallback<List<Music>>) music -> {
                // 存储到本地数据库
                Factory.getMusicCenter().dispatch(music);
            });
        }
    }

    /**
     * 跳转到主界面
     */
    private void skip() {
        startAnim(0.5f, () -> {
            reallySkip();
        });
    }

    /**
     * 跳转方法
     */
    private void reallySkip() {
        MainActivity.show(this);
        finish();
    }

    private void startAnim(float endProgress, final Runnable endCallback) {
        // 获取一个最终的颜色
        int finalColor = Resource.Color.WHITE;
        // 运算当前进度的颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);

        // 构建一个属性动画
        ValueAnimator animator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        animator.setDuration(1500);
        animator.setIntValues(mBgDrawable.getColor(), endColor); // 开始结束值
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 结束时触发
                endCallback.run();
            }
        });
        animator.start();
    }

    private Property<LaunchActivity, Object> property = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }

        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }
    };

    @Override
    public void onDataSave(List<Music> list) {
        // 修改成在数据库存储完成后的回调中，进行界面跳转及..
        // 开启Service
        Run.onUiAsync(() -> {
            startService(new Intent(LaunchActivity.this, MusicService.class));
            // 跳转到主界面
            skip();

        });

    }

    @Override
    public void onDataDelete(List<Music> list) {

    }

    @Override
    protected void onDestroy() {
        // 注销对数据库的注册
        DbHelper.removeChangedListener(Music.class, this);
        super.onDestroy();
    }
}
