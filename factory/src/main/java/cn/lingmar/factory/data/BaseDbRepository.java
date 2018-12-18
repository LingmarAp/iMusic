package cn.lingmar.factory.data;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.qiujuer.genius.kit.reflect.Reflector;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import cn.lingmar.factory.data.helper.DbHelper;
import cn.lingmar.factory.model.BaseDbModel;

/**
 * 基础的数据库仓库
 * 实现对数据库基本的监听操作
 */
public abstract class BaseDbRepository<Data extends BaseDbModel> implements DbDataSource<Data>,
        DbHelper.ChangedListener<Data>,
        QueryTransaction.QueryResultListCallback<Data> {
    private SucceedCallback<List<Data>> callback;
    private Class<Data> dataClass;
    public final LinkedList<Data> dataList = new LinkedList<>();

    public BaseDbRepository() {
        // 拿到当前类的泛型数组信息
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class, this.getClass());
        dataClass = (Class<Data>) types[0];
    }

    @Override
    public void load(SucceedCallback<List<Data>> callback) {
        this.callback = callback;
        // 添加数据库监听
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        this.callback = null;
        DbHelper.removeChangedListener(dataClass, this);
        dataList.clear();
    }

    // 数据库统一通知：增加 /更改
    @Override
    public void onDataSave(List<Data> list) {
        boolean isChanged = false;
        for (Data data : list) {
            if (isRequired(data)) {
                insertOrUpdate(data);
                isChanged = true;
            }
        }
        // 判断是否有数据要删除
        boolean isFind;
        for (Data data : dataList) {
            isFind = false;
            for (Data d : list) {
                if (d.isSame(data)) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                dataList.remove(data);
                isChanged = true;
            }
        }

        if (isChanged)
            notifyDataChange();
    }

    // 数据库统一通知：删除
    @Override
    public void onDataDelete(List<Data> list) {
        boolean isChanged = false;
        for (Data data : list) {
            if (dataList.remove(data))
                isChanged = true;
        }

        if (isChanged)
            notifyDataChange();
    }

    // DbFlow框架通知的回调
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        // 数据库加载数据成功
        if (tResult.size() == 0) {
            dataList.clear();
            notifyDataChange();
            return;
        }
        // 回到数据集更新的操作中
        onDataSave(tResult);
    }

    private void insertOrUpdate(Data data) {
        int index = indexOf(data);
        if (index >= 0) {
            replace(index, data);
        } else {
            insert(data);
        }
    }

    protected void insert(Data data) {
        dataList.add(data);
    }

    protected void replace(int index, Data data) {
        dataList.remove(index);
        dataList.add(index, data);
    }

    protected int indexOf(Data newData) {
        int index = -1;
        for (Data data : dataList) {
            index++;
            if (data.isSame(newData)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * 检查是否是我需要关注的数据
     *
     * @param data Data
     * @return True
     */
    protected abstract boolean isRequired(Data data);

    /**
     * 添加数据库的监听
     */
    protected void registerDbChangedListener() {
        DbHelper.addChangedListener(dataClass, this);
    }

    private void notifyDataChange() {
        SucceedCallback<List<Data>> callback = this.callback;
        if (callback != null)
            callback.onDataLoaded(dataList);
    }
}
