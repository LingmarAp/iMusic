package cn.lingmar.factory.presenter;

import android.content.Intent;

import java.util.List;

import cn.lingmar.common.app.Activity;
import cn.lingmar.factory.Factory;
import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.data.music.MusicRepository;
import cn.lingmar.factory.model.Music;
import cn.lingmar.factory.receiver.MusicReceiver;
import cn.lingmar.factory.service.MusicService;

public class MusicPresenter
        extends BaseSourcePresenter<Music, Music, MusicRepository, MusicContract.View>
        implements MusicContract.Presenter, MusicReceiver.ReceiverListener {

    public MusicPresenter(MusicContract.View view) {
        super(new MusicRepository(), view);
        MusicReceiver.addReceiverListener(this);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void onDataLoaded(List<Music> music) {
        refreshData(music);

        // 通知Service刷新曲库
        Intent intent = new Intent(((Activity) getView()).getApplicationContext(),
                MusicService.class);
        intent.putExtra(MusicService.MUSIC_OPERATE, MusicService.REFRESH_MUSIC);
        ((Activity) getView()).startService(intent);
    }

    @Override
    public void doMusicPlay(Music music, int index) {
        // 调用Service播放音乐
        Intent intent = new Intent(((Activity) getView()).getApplicationContext(),
                MusicService.class);
        intent.putExtra(MusicService.MUSIC_OPERATE, MusicService.START_MUSIC);
        intent.putExtra(MusicService.MUSIC_INDEX, index);
        ((Activity) getView()).startService(intent);

        // TODO 模拟Service发送的广播

        getView().onMusicPlay(music, index);
    }

    @Override
    public void doMusicStartOrPause() {
        Intent intent = new Intent(((Activity) getView()).getApplicationContext(),
                MusicService.class);
        intent.putExtra(MusicService.MUSIC_OPERATE, MusicService.PLAY_OR_PAUSE_MUSIC);
        ((Activity) getView()).startService(intent);

        // TODO 模拟Service发送的广播
        if (MusicService.isPlay()) {
            getView().onMusicResume();
        } else {
            getView().onMusicPause();
        }
    }

    @Override
    public void doRefresh() {
        Factory.getMusicHelper().refresh(((Activity) getView()).getApplicationContext(),
                (DataSource.SucceedCallback<List<Music>>) music -> Factory.getMusicCenter().dispatch(music));
    }

    @Override
    public void doInit() {
        Intent intent = new Intent(((Activity) getView()).getApplicationContext(), MusicService.class);
        intent.putExtra(MusicService.MUSIC_OPERATE, MusicService.INIT_MUSIC);
        ((Activity) getView()).getApplicationContext().startService(intent);
    }

    @Override
    public void onDataReceiver() {
        MusicContract.View view = getView();
        view.onMusicPlay(MusicService.getMusic(), MusicService.getCurrentMusicIndex());;
//        view.onMusicChange(MusicService.getCurrentMusicIndex());
        if (!MusicService.isPlay())
            view.onMusicResume();
        else
            view.onMusicPause();
    }
}
