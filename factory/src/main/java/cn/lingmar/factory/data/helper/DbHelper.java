package cn.lingmar.factory.data.helper;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.lingmar.factory.model.AppDatabase;

/**
 * 数据库的辅助工具类
 * 辅助完成：增删改
 */
public class DbHelper {
    private static final DbHelper instance;

    static {
        instance = new DbHelper();
    }

    /**
     * 观察者集合
     * Class<?>：观察的表
     * Set<ChangedListener>：每张表对应的不同的观察者
     */
    private final Map<Class<?>, Set<ChangedListener>> changedListeners = new HashMap<>();

    private <Model extends BaseModel> Set<ChangedListener> getListeners(Class<Model> tClass) {
        if (changedListeners.containsKey(tClass))
            return changedListeners.get(tClass);
        return null;
    }

    /**
     * 添加一个监听
     *
     * @param tClass   对某个表关注
     * @param listener 监听者
     * @param <Model>  表的泛型
     */
    public static <Model extends BaseModel> void addChangedListener(final Class<Model> tClass,
                                                                    ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            changedListeners = new HashSet<>();
            instance.changedListeners.put(tClass, changedListeners);
        }
        changedListeners.add(listener);
    }

    public static <Model extends BaseModel> void removeChangedListener(final Class<Model> tClass,
                                                                       ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            return;
        }
        changedListeners.remove(listener);
    }

    /**
     * 新增或修改的统一方法
     *
     * @param tClass  传递Class信息
     * @param models  Class对应的实例数组
     * @param <Model> 这个实例的泛型
     */
    public static <Model extends BaseModel> void save(final Class<Model> tClass, final List<Model> models) {
        if (models == null || models.size() == 0)
            return;

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(databaseWrapper -> {
            FlowManager.getModelAdapter(tClass)
                    .saveAll(models);
            // 唤起通知
            instance.notifySave(tClass, models);
        }).build().execute();
    }

    /**
     * 删除表中所有信息
     */
    public static <Model extends BaseModel> void deleteAll(final Class<Model> tClass) {
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(databaseWrapper -> SQLite.delete(tClass)
                .execute()).build().execute();
    }

    /**
     * 查询表中所有信息
     */
    public static <Model extends BaseModel> List<Model> findAll(final Class<Model> tClass) {
        return SQLite.select()
                .from(tClass)
                .where()
                .queryList();//返回的list不为空，但是可能为empty
    }

    /**
     * 进行通知调用
     *
     * @param tClass  通知的类型
     * @param models  通知的Model数组
     * @param <Model> 这个实例的泛型
     */
    @SuppressWarnings("unchecked")
    public final <Model extends BaseModel> void notifySave(Class<Model> tClass,
                                                           List<Model> models) {
        Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataSave(models);
            }
        }
    }

    /**
     * 进行通知调用
     *
     * @param tClass  通知的类型
     * @param models  通知的Model数组
     * @param <Model> 这个实例的泛型
     */
    @SuppressWarnings("unchecked")
    public final <Model extends BaseModel> void notifyDelete(Class<Model> tClass,
                                                             List<Model> models) {
        Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataDelete(models);
            }
        }
    }

    /**
     * 通知监听器
     */
    public interface ChangedListener<Data extends BaseModel> {
        void onDataSave(List<Data> list);

        void onDataDelete(List<Data> list);
    }

}
