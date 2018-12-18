package cn.lingmar.factory.presenter;

public class BasePresenter<T extends BaseContract.View>
        implements BaseContract.Presenter {

    private T mView;
    public BasePresenter(T view) {
        setView(view);
    }

    /**
     * 设置一个View，子类可以复写
     * @param view view
     */
    @SuppressWarnings("unchecked")
    protected void setView(T view) {
        this.mView = view;
        this.mView.setPresenter(this);
    }

    /**
     * 给子类使用的获取View的操作
     * 不允许复写
     * @return mView
     */
    protected final T getView() {
        return mView;
    }

    @Override
    public void start() {
        // 开始的时候进行Loading调用
        T view = mView;
        if(view != null) {
            view.showLoading();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void destroy() {
        T view = mView;
        mView = null;
        if(view != null) {
            // 把Presenter设置为NULL
            view.setPresenter(null);
        }
    }
}
