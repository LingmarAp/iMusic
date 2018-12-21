package cn.lingmar.factory.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import net.qiujuer.genius.kit.handler.Run;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.lingmar.common.app.Application;
import cn.lingmar.factory.R;
import cn.lingmar.factory.data.helper.DbHelper;
import cn.lingmar.factory.model.Music;
import cn.lingmar.factory.receiver.MusicChangeReceiver;
import cn.lingmar.factory.receiver.MusicProgressReceiver;
import cn.lingmar.factory.receiver.MusicReceiver;

public class MusicService extends Service
        implements MediaPlayer.OnCompletionListener {

    // 要进行操作
    public static final int START_MUSIC = -1;
    public static final int PLAY_OR_PAUSE_MUSIC = 0;
    public static final int STOP_MUSIC = 1;
    public static final int REFRESH_MUSIC = 3;
    public static final int INIT_MUSIC = 5;
    public static final int NEXT_MUSIC = 6;
    public static final int PRE_MUSIC = 7;
    public static final int SEEK_TO_MUSIC = 8;
    public static final int CHANGE_PLAY_MODE = 9;

    // 操作的具体内容
    public static final String MUSIC_OPERATE = "music_operate";
    public static final String MUSIC_INDEX = "music_index";
    public static final String MUSIC_MODE = "music_mode";

    public static final int STATE_MUSIC_LOOP = 0;
    public static final int STATE_MUSIC_LOOP_1 = 1;
    public static final int STATE_MUSIC_RANDOM = 2;

    public static final String MUSIC_PROGRESS_POSITION = "music_progress_position";
    public static final String MUSIC_SEEK_POSITION = "music_seek_position";

    public static final List<Music> musics = new ArrayList<>();

    private static MediaPlayer mediaPlayer;

    // 歌曲播放模式
    private static int playMode = STATE_MUSIC_LOOP;

    // 播放状态的Flag
    private static boolean isPlay = false;
    private static boolean playFlag = true;

    // 当前播放的Music
    private static Music music = null;
    // 当前播放的位置
    private static int index = 0;

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
        // 注册歌曲播放事件
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int tag = intent.getIntExtra(MUSIC_OPERATE, STOP_MUSIC);
        switch (tag) {
            case START_MUSIC:
                index = intent.getIntExtra(MUSIC_INDEX, 0);
                initMusic(index, false);
                mediaPlayer.start();
                sendPlayOrPause();
                break;
            case PLAY_OR_PAUSE_MUSIC:
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.pause();
                    else
                        mediaPlayer.start();
                    sendPlayOrPause();
                }

                isPlay = !isPlay;
                break;
            case REFRESH_MUSIC:
                refresh();
                break;
            case INIT_MUSIC:
                initMusic(0, true);
                // 动态注册广播
                registerReceiver(new MusicProgressReceiver(), progressFilter());
                registerReceiver(new MusicReceiver(), dataSourceFilter());
                registerReceiver(new MusicChangeReceiver(), changeFilter());
                sendProgress();
                break;
            case NEXT_MUSIC:
                playNextMusic();
                break;
            case SEEK_TO_MUSIC:
                if (mediaPlayer != null) {
                    int position = intent.getIntExtra(MUSIC_SEEK_POSITION, 0);
                    mediaPlayer.seekTo(position);
                }
                break;
            case CHANGE_PLAY_MODE:
                int mode = intent.getIntExtra(MUSIC_MODE, STATE_MUSIC_LOOP);
                playMode = mode;
                break;
            default:
                // Application.showToast(R.string.data_music_wait);
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public static boolean isPlay() {
        return isPlay;
    }

    public static Music getMusic() {
        if (music == null && musics.size() != 0)
            music = musics.get(0);
        return music;
    }

    public static int getCurrentMusicIndex() {
        return index;
    }

    public static int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public static int getCurrentMode() {
        return playMode;
    }

    private void initMusic(int index, boolean flag) {
        // 初始化Flag的值
        isPlay = flag;
        // 参数校验
        if (index < 0 || index > musics.size() - 1) return;

        music = musics.get(index);
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        try {
            music = musics.get(index);

            mediaPlayer.setDataSource(this, Uri.parse(music.getPath()));
            mediaPlayer.prepare();
        } catch (IOException e) {
            Application.showToast(R.string.data_music_play_error);
            e.printStackTrace();
        }
    }

    private void playNextMusic() {
        // 判断将要播放的歌曲类型
        switch (playMode) {
            case STATE_MUSIC_LOOP:
                index++;
                break;
            case STATE_MUSIC_LOOP_1:
                break;
            case STATE_MUSIC_RANDOM:
                Random random = new Random();
                index = random.nextInt(musics.size());
                break;
        }
        initMusic(index % musics.size(), false);
        mediaPlayer.start();
        sendChangeMusic();
        sendPlayOrPause();
    }

    private void refresh() {
        Run.onBackground(() -> {
            musics.clear();
            List<Music> musics = DbHelper.findAll(Music.class);
            MusicService.musics.addAll(musics);
        });
    }

    private void sendChangeMusic() {
        if (mediaPlayer == null)
            return;

        Intent intent = new Intent("cn.lingmar.android.MUSIC_CHANGE_BROADCAST");
        sendBroadcast(intent);
    }

    private void sendPlayOrPause() {
        if (mediaPlayer == null)
            return;

        Intent intent = new Intent("cn.lingmar.android.MUSIC_BROADCAST");
        intent.putExtra(MUSIC_PROGRESS_POSITION, mediaPlayer.getCurrentPosition());
        sendBroadcast(intent);
    }

    // 循环发送当前播放状态的广播
    private void sendProgress() {
        if (mediaPlayer == null)
            return;

        new Thread(() -> {
            while (playFlag) {
                Intent intent = new Intent("cn.lingmar.android.MUSIC_PROGRESS_BROADCAST");
                intent.putExtra(MUSIC_PROGRESS_POSITION, mediaPlayer.getCurrentPosition());
                sendBroadcast(intent);
                try {
                    Thread.sleep(500);
                } catch (Exception ignore) {
                }
            }
        }).start();

        /*Run.onBackground(() -> {

        });*/
    }

    private static IntentFilter progressFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.lingmar.android.MUSIC_PROGRESS_BROADCAST");
        return intentFilter;
    }

    private static IntentFilter changeFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.lingmar.android.MUSIC_CHANGE_BROADCAST");
        return intentFilter;
    }

    private static IntentFilter dataSourceFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.lingmar.android.MUSIC_BROADCAST");
        return intentFilter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playFlag = false;
    }

    // 歌曲播放完成的回调
    @Override
    public void onCompletion(MediaPlayer mp) {
        playNextMusic();
    }
}
