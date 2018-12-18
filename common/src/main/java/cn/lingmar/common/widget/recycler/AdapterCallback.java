package cn.lingmar.common.widget.recycler;

/**
 * Created by Lingmar on 2017/10/26.
 */

public interface AdapterCallback<Data> {
    void update(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
