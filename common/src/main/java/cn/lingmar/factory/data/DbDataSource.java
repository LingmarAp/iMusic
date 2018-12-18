package cn.lingmar.factory.data;

import java.util.List;

/**
 * 基础的数据库数据源接口定义
 * @param <Data>
 */
public interface DbDataSource<Data> extends DataSource {
    /**
     * 基本的数据源加载方法
     * @param callback 回调到Presenter中
     */
    void load(SucceedCallback<List<Data>> callback);
}
