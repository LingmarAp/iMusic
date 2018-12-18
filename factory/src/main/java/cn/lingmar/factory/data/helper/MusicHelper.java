package cn.lingmar.factory.data.helper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.model.Music;
import cn.lingmar.factory.utils.MusicUtil;

public class MusicHelper {
    private static MusicHelper instance;
    // 单线程池
    private static final Executor executor = Executors.newSingleThreadExecutor();

    private MusicHelper() {
    }

    public static MusicHelper instance() {
        if (instance == null) {
            synchronized (MusicHelper.class) {
                if (instance == null)
                    instance = new MusicHelper();
            }
        }

        return instance;
    }

    public void refresh(Context context, DataSource.SucceedCallback callback) {
        executor.execute(new RefreshRunnable(context, callback));
    }

    class RefreshRunnable implements Runnable {
        Context context;
        DataSource.SucceedCallback callback;

        public RefreshRunnable(Context context, DataSource.SucceedCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            List<Music> musics = new ArrayList<>();
            MusicUtil.scanMusic(context, musics);
            callback.onDataLoaded(musics);
        }
    }

    public static void update() {

    }

}
