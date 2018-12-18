package cn.lingmar.factory.data.music;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.lingmar.factory.data.helper.DbHelper;
import cn.lingmar.factory.model.Music;

public class MusicDispatcher implements MusicCenter {
    private static MusicCenter instance;
    // 单线程池；线程队列里一个个消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static MusicCenter instance() {
        if (instance == null) {
            synchronized (MusicDispatcher.class) {
                if (instance == null)
                    instance = new MusicDispatcher();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(List<Music> musics) {
        if (musics == null || musics.size() == 0)
            return;

        executor.execute(new MusicHandler(musics));
    }

    /**
     * 线程调度的时候就会触发run方法
     */
    private class MusicHandler implements Runnable {
        private List<Music> musics;

        public MusicHandler(List<Music> musics) {
            this.musics = musics;
        }

        @Override
        public void run() {
            // 删除所有数据
            DbHelper.deleteAll(Music.class);

            // 不需要重复过滤操作
            DbHelper.save(Music.class, musics);
        }
    }

}
