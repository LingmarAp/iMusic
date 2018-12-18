package cn.lingmar.factory.presenter;

import android.support.annotation.StringRes;

import cn.lingmar.common.widget.recycler.RecyclerAdapter;

/**
 * MVP模式中公共的基本契约
 */
public interface BaseContract {
    // 基本的View职责
    interface View<T extends Presenter> {

        // 公共的：显示一个字符串错误
        void showError(@StringRes int str);

        // 公共的：显示进度条
        void showLoading();

        // 支持设置一个Presenter
        void setPresenter(T presenter);
    }

    // 基本的Presenter职责
    interface Presenter {

        // 公用的开始方法
        void start();

        // 公用的销毁触发
        void destroy();
    }

    // 基本的列表View职责
    interface RecyclerView<T extends Presenter, ViewMode> extends View<T> {
        // 拿到一个适配器，然后自己自主的刷新
        RecyclerAdapter<ViewMode> getRecyclerAdapter();

        // 当适配器数据更改了的时候触发
        void onAdapterDataChanged();
    }
}
