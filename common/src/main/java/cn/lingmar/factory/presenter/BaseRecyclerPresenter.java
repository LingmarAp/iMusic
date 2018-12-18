package cn.lingmar.factory.presenter;

import android.support.v7.util.DiffUtil;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import cn.lingmar.common.widget.recycler.RecyclerAdapter;

/**
 * 对RecyclerView进行的一个简单的Presenter封装
 *
 * @param <ViewModel>
 * @param <View>
 */
public class BaseRecyclerPresenter<ViewModel, View extends BaseContract.RecyclerView>
        extends BasePresenter<View> {

    public BaseRecyclerPresenter(View view) {
        super(view);
    }

    /**
     * 刷新新数据到界面中
     *
     * @param dataList 新数据
     */
    protected void refreshData(final List<ViewModel> dataList) {
        Run.onUiAsync(() -> {
            View view = getView();
            if (view == null)
                return;

            // 基本的更新数据并刷新界面
            RecyclerAdapter<ViewModel> adapter = view.getRecyclerAdapter();
            adapter.replace(dataList);
            view.onAdapterDataChanged();
        });
    }

    /**
     * 刷新界面操作，该操作可以保证执行方法在主线程进行
     *
     * @param diffResult 一个差异的结果集
     * @param dataList   具体的新数据
     */
    protected void refreshData(final DiffUtil.DiffResult diffResult,
                               final List<ViewModel> dataList) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                refreshDataOnUiThread(diffResult, dataList);
            }
        });
    }

    protected void refreshDataOnUiThread(final DiffUtil.DiffResult diffResult,
                                         final List<ViewModel> dataList) {
        View view = getView();
        if (view == null)
            return;

        // 基本的更新数据并刷新界面
        RecyclerAdapter<ViewModel> adapter = view.getRecyclerAdapter();
        adapter.getItems().clear();
        adapter.getItems().addAll(dataList);
        // 通知界面刷新占位布局
        view.onAdapterDataChanged();

        diffResult.dispatchUpdatesTo(adapter);
    }
}
