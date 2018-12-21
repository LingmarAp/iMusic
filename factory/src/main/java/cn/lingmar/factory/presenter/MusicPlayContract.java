package cn.lingmar.factory.presenter;

public interface MusicPlayContract {

    interface Presenter extends BaseContract.Presenter {
        // 开始或暂停音乐
        void doMusicStartOrPause();

        void doMusicNext();

        void doMusicPre();

        void doMusicChange();

        void removeListener();

        void seekPosition(int position);
    }

    interface View extends BaseContract.View<Presenter> {

        // 歌曲重新开始的回调
        void onMusicResume();

        // 歌曲暂停的回调
        void onMusicPause();

        // 改变SeekBar的进度
        void onChangeProgress(int position);

        void onNext();

        void onPre();
    }
}
