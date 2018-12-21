package cn.lingmar.common.app;

import android.support.v4.widget.SwipeRefreshLayout;

import cn.lingmar.factory.presenter.BaseContract;

public abstract class PresenterListActivity<Presenter extends BaseContract.Presenter>
        extends PresenterActivity<Presenter> {
    private SwipeRefreshLayout refresh;

    @Override
    protected void initData() {
        super.initData();
        refresh = initRefresh();
    }

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

}
