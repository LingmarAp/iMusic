package cn.lingmar.factory.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import net.qiujuer.genius.kit.handler.Run;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.lingmar.common.app.Application;
import cn.lingmar.factory.R;
import cn.lingmar.factory.data.helper.DbHelper;
import cn.lingmar.factory.model.Music;

public class MusicService extends Service {

    public static final int START_MUSIC = -1;
    public static final int PLAY_OR_PAUSE_MUSIC = 0;
    public static final int STOP_MUSIC = 1;
    public static final int REFRESH_MUSIC = 3;

    public static final String MUSIC_OPERATE = "music_operate";
    public static final String MUSIC_INDEX = "music_index";

    public static final List<Music> musics = new ArrayList<>();
    public MediaPlayer mediaPlayer;

    // 播放状态的Flag
    private static boolean isPlay = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 从数据库中读取歌曲清单
        refresh();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int tag = intent.getIntExtra(MUSIC_OPERATE, STOP_MUSIC);
        switch (tag) {
            case START_MUSIC:
                // 初始化Flag的值
                isPlay = false;

                int index = intent.getIntExtra(MUSIC_INDEX, 0);
                // 参数校验
                if (index < 0 || index > musics.size() - 1) break;

                Music music = musics.get(index);
                // 判断图片是否已加载
                if(music.getMusicCover() == null)
                    music.setMusicCover(Application.getInstance());

                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                }
                try {
                    mediaPlayer.setDataSource(this, Uri.parse(music.getPath()));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    Application.showToast(R.string.data_music_play_error);
                    e.printStackTrace();
                }
                break;
            case PLAY_OR_PAUSE_MUSIC:
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.pause();
                    else
                        mediaPlayer.start();
                }
                isPlay = !isPlay;
                break;
            case REFRESH_MUSIC:
                refresh();
                break;
            default:
                Application.showToast(R.string.data_music_wait);
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static boolean isPlay() {
        return isPlay;
    }

    private void refresh() {
        Run.onBackground(() -> {
            musics.clear();
            List<Music> musics = DbHelper.findAll(Music.class);
            MusicService.musics.addAll(musics);
        });
    }
}
