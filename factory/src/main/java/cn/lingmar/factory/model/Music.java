package cn.lingmar.factory.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.Serializable;
import java.util.Objects;

import cn.lingmar.factory.utils.MusicUtil;

@Table(database = AppDatabase.class)
public class Music extends BaseDbModel<Music> implements Serializable {

    @PrimaryKey
    private long id;

    @Column
    private long albumId;

    @Column
    private long duration;

    @Column
    private long fileSize;

    @Column
    private String title;

    @Column
    private String artist;

    @Column
    private String album;

    @Column
    private String path;

    @Column
    private String fileName;

    private Bitmap musicCover;

    private Bitmap musicOriginalCover;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Bitmap getMusicCover(Context context) {
        if (this.musicCover == null)
            this.musicCover = MusicUtil.loadCover(context, id, albumId, 150, 150);
        return this.musicCover;
    }

    public Bitmap getMusicOriginalCover(Context context) {
        if (this.musicOriginalCover == null)
            this.musicOriginalCover = MusicUtil.loadCover(context, id, albumId, 500, 500);
        return this.musicOriginalCover;
    }

    public void setMusicCover(Bitmap musicCover) {
        this.musicCover = musicCover;
    }

    public String getDescription() {
        StringBuffer buf = new StringBuffer();
        if (!TextUtils.isEmpty(artist))
            buf.append(artist + "·");
        if (!TextUtils.isEmpty(album))
            buf.append(album + "·");
        return buf.substring(0, buf.length() == 0 ? 0 : buf.length() - 1);
    }

    public static boolean check(Music model) {
        return !TextUtils.isEmpty(model.album)
                && !model.album.contains("Record")
                && !model.album.contains("Download")
                && !model.album.contains("record")
                && !model.album.contains("speech")
                && !model.album.contains("call")
                && !model.album.contains("audio");
    }

    @Override
    public boolean isSame(Music old) {
        return Objects.equals(id, old.id);
    }

    @Override
    public boolean isUiContentSame(Music old) {
        return this == old ||
                Objects.equals(title, old.title)
                        && Objects.equals(artist, old.artist)
                        && Objects.equals(album, old.album)
                ;
    }
}
