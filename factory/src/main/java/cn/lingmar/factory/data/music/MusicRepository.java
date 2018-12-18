package cn.lingmar.factory.data.music;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import cn.lingmar.factory.data.BaseDbRepository;
import cn.lingmar.factory.model.Music;

public class MusicRepository extends BaseDbRepository<Music> {
    @Override
    public void load(SucceedCallback<List<Music>> callback) {
        super.load(callback);

        // 读取数据库
        SQLite.select()
                .from(Music.class)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Music music) {
        return true;
    }
}
