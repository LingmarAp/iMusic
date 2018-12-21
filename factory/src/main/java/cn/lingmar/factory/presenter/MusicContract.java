package cn.lingmar.factory.presenter;

import cn.lingmar.factory.model.Music;

public interface MusicContract {

    interface Presenter extends BaseContract.Presenter {
        // 播放音乐
        void doMusicPlay(Music music, int index);

        // 暂停音乐
        void doMusicStartOrPause();

        // 重新扫描本地曲库
        void doRefresh();

        // 初始化
        void doInit();
    }

    interface View extends BaseContract.RecyclerView<Presenter, Music> {
        // 歌曲播放的回调
        void onMusicPlay(Music music, int index);

        // 歌曲重新开始的回调
        void onMusicResume();

        // 歌曲暂停的回调
        void onMusicPause();

        void onMusicChange(int index);
    }
}
