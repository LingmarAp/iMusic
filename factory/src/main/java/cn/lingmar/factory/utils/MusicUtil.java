package cn.lingmar.factory.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.List;

import cn.lingmar.common.app.Activity;
import cn.lingmar.common.tools.UITool;
import cn.lingmar.factory.R;
import cn.lingmar.factory.model.Music;
import cn.lingmar.utils.FastBlurUtil;

public class MusicUtil {
    /**
     * 扫描歌曲
     */
    public static void scanMusic(Context context, List<Music> musicList) {
        // 强制刷新媒体库
        MediaScannerConnection.scanFile(context, new String[]{Environment
                .getExternalStorageDirectory().getAbsolutePath()}, null, null);

        musicList.clear();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        MediaStore.Audio.AudioColumns.IS_MUSIC,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                        MediaStore.Audio.AudioColumns.SIZE,
                        MediaStore.Audio.AudioColumns.DURATION
                },
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            // 是否为音乐
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic == 0) {
                continue;
            }
            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            // 标题
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
            // 艺术家
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            // 专辑
            String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            // 持续时间
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            // 音乐文件路径
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            // 音乐文件名
            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
            // 音乐文件大小
            long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
//            Bitmap cover = loadCover(context, id, albumId);

            Music music = new Music();

            // 保存到Music对象中
            music.setId(id);
            music.setTitle(title);
            music.setAlbum(album);
            music.setAlbumId(albumId);
            music.setDuration(duration);
            music.setArtist(artist);
            music.setPath(path);
            music.setFileName(fileName);
            music.setFileSize(fileSize);
//            music.setMusicCover(cover);

            // 检查音乐规范性
            if (Music.check(music))
                musicList.add(music);
        }
        cursor.close();
    }

    private static final Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");

    /**
     * 从媒体库加载封面
     */
    public static Bitmap loadCover(Context context, long songId, long albumId, int width, int height) {
        Bitmap bm = null;
        // 专辑id和歌曲id小于0说明没有专辑、歌曲，并抛出异常
        if (albumId < 0 && songId < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            if (albumId < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songId + "/albums");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);


                } else {
                    return null;
                }
            }
        } catch (FileNotFoundException ex) {
        } //如果获取的bitmap为空，则返回一个默认的bitmap
        if (bm == null) {
            Resources resources = context.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.default_music_logo);
            //Drawable 转 Bitmap
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bm = bitmapDrawable.getBitmap();
        }
        return Bitmap.createScaledBitmap(bm, width, height, true);
    }

    /**
     * 格式化时间，将毫秒转换为分:秒格式//将long类型转化为String型
     */
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    public static Drawable getForegroundDrawable(Activity activity, Bitmap bitmap) {
        Bitmap mBitmap = bitmap;
        // 得到屏幕的宽高比，以便按比例切割图片一部分
        final float widthHeightRate = (float) ((float) (UITool.getScreenWidth(activity)) * 1.0 /
                (UITool.getScreenHeight(activity)) * 1.0);

        int cropBitmapWidth = (int) (widthHeightRate * mBitmap.getHeight());
        // 切割部分图片
        Bitmap newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, cropBitmapWidth,
                mBitmap.getHeight());
        // 缩小图片
        if (newBitmap.getWidth() > 5000)
            newBitmap = Bitmap.createScaledBitmap(newBitmap, mBitmap.getWidth() / 50, mBitmap
                    .getHeight() / 50, false);

        // 模糊化
        final Bitmap blurBitmap = FastBlurUtil.doBlur(newBitmap, 8, true);
        final Drawable foregroundDrawable = new BitmapDrawable(blurBitmap);
        // 加入灰色遮罩层，避免图片过亮影响其他控件
        foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        return foregroundDrawable;
    }
}
