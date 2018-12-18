package cn.lingmar.common.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.bumptech.glide.RequestManager;

import java.io.ByteArrayOutputStream;

import cn.lingmar.common.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 圆形音乐图片控件
 */
public class MusicImageView extends CircleImageView {
    public MusicImageView(Context context) {
        super(context);
    }

    public MusicImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setup(RequestManager manager, Bitmap bitmap) {
        setup(manager, R.drawable.default_music_logo, bitmap);
    }

    public void setup(RequestManager manager, int resourceId, Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();

        manager.load(bytes)
                .placeholder(resourceId)
                .centerCrop()
                .dontAnimate()
                .into(this);
    }
}
