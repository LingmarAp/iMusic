package cn.lingmar.common.app;

import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;

import cn.lingmar.common.widget.MusicImageView;
import cn.lingmar.factory.presenter.BaseContract;

public abstract class PresenterActivity<Presenter extends BaseContract.Presenter> extends Activity
        implements BaseContract.View<Presenter> {
    protected Presenter mPresenter;
    protected ObjectAnimator animator;

    @Override
    protected void initBefore() {
        super.initBefore();

        // 初始化Presenter
        mPresenter = initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    protected abstract Presenter initPresenter();

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showError(int str) {
        Application.showToast(str);
    }

    @Override
    public void showLoading() {
    }

    // 开始音乐播放动画
    protected void startPlayAnim(MusicImageView mMusicImg) {
        if (animator != null)
            animator.cancel();

        animator = ObjectAnimator.ofFloat(mMusicImg, "rotation", 0f, 360f);
        animator.setDuration(12000);
        animator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.start();
    }
}
