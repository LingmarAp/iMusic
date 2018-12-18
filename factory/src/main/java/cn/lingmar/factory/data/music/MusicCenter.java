package cn.lingmar.factory.data.music;

import java.util.List;

import cn.lingmar.factory.model.Music;

public interface MusicCenter {

    void dispatch(List<Music> musics);
}
