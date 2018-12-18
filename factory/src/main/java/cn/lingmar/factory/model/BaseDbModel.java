package cn.lingmar.factory.model;

import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.lingmar.factory.utils.DiffUiDataCallback;

/**
 * 继承DbFlow中的基础类
 * 同时定义需要的方法
 */
public abstract class BaseDbModel<Model> extends BaseModel
        implements DiffUiDataCallback.UiDataDiff<Model> {
}
