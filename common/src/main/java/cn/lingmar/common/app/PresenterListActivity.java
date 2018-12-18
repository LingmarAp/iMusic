package cn.lingmar.common.app;

import android.support.v4.widget.SwipeRefreshLayout;

import cn.lingmar.factory.presenter.BaseContract;

public abstract class PresenterListActivity<Presenter extends BaseContract.Presenter> extends Activity
        implements BaseContract.View<Presenter> {
    protected Presenter mPresenter;
    private SwipeRefreshLayout refresh;

    @Override
    protected void initBefore() {
        super.initBefore();

        // 初始化Presenter
        mPresenter = initPresenter();
    }

    @Override
    protected void initData() {
        super.initData();
        refresh = initRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    protected abstract Presenter initPresenter();

    protected abstract SwipeRefreshLayout initRefresh();

    @Override
    public void showError(int str) {
        hideLoading();
        Application.showToast(str);
    }

    @Override
    public void showLoading() {
        if (!refresh.isRefreshing()) {
            refresh.setRefreshing(true);
        }
    }

    protected void hideLoading() {
        if (refresh.isRefreshing()) {
            refresh.setRefreshing(false);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }
}
