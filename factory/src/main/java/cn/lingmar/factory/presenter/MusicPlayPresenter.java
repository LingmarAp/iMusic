package cn.lingmar.factory.presenter;

import android.content.Intent;

import cn.lingmar.common.app.Activity;
import cn.lingmar.common.app.Application;
import cn.lingmar.factory.R;
import cn.lingmar.factory.receiver.MusicChangeReceiver;
import cn.lingmar.factory.receiver.MusicProgressReceiver;
import cn.lingmar.factory.receiver.MusicReceiver;
import cn.lingmar.factory.service.MusicService;

public class MusicPlayPresenter
        extends BasePresenter<MusicPlayContract.View>
        implements MusicPlayContract.Presenter,
        MusicProgressReceiver.ReceiverListener<Integer>,
        MusicReceiver.ReceiverListener,
        MusicChangeReceiver.ReceiverListener {

    public MusicPlayPresenter(MusicPlayContract.View view) {
        super(view);
        // 注册广播的监听事件
        MusicProgressReceiver.addReceiverListener(this);
        MusicReceiver.addReceiverListener(this);
        MusicChangeReceiver.addReceiverListener(this);
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
    public void doMusicNext() {
        Intent intent = new Intent(((Activity) getView()).getApplicationContext(),
                MusicService.class);
        intent.putExtra(MusicService.MUSIC_OPERATE, MusicService.NEXT_MUSIC);
        ((Activity) getView()).startService(intent);
    }

    @Override
    public void doMusicPre() {

    }

    @Override
    public void doMusicChange() {
        Intent intent = new Intent(((Activity) getView()).getApplicationContext(),
                MusicService.class);
        intent.putExtra(MusicService.MUSIC_OPERATE, MusicService.CHANGE_PLAY_MODE);
        int mode = (MusicService.getCurrentMode() + 1) % 3;
        intent.putExtra(MusicService.MUSIC_MODE, mode);
        ((Activity) getView()).startService(intent);

        switch (mode) {
            case MusicService.STATE_MUSIC_LOOP:
                Application.showToast(R.string.data_music_loop_done);
                break;
            case MusicService.STATE_MUSIC_LOOP_1:
                Application.showToast(R.string.data_music_loop_1_done);
                break;
            case MusicService.STATE_MUSIC_RANDOM:
                Application.showToast(R.string.data_music_random_done);
                break;
        }
    }

    @Override
    public void removeListener() {
        MusicProgressReceiver.removeReceiverListener(this);
        MusicReceiver.removeReceiverListener(this);
        MusicChangeReceiver.removeReceiverListener(this);
    }

    @Override
    public void seekPosition(int position) {
        Intent intent = new Intent(((Activity) getView()).getApplicationContext(),
                MusicService.class);
        intent.putExtra(MusicService.MUSIC_OPERATE, MusicService.SEEK_TO_MUSIC);
        intent.putExtra(MusicService.MUSIC_SEEK_POSITION, position);
        ((Activity) getView()).startService(intent);
    }

    @Override
    public void onProgressReceiver(Integer position) {
        getView().onChangeProgress(position);
    }

    @Override
    public void onDataReceiver() {
        if (!MusicService.isPlay())
            getView().onMusicResume();
        else
            getView().onMusicPause();
    }

    @Override
    public void onChangeReceiver() {
        // 通知界面播放动画
        getView().onNext();
    }
}
