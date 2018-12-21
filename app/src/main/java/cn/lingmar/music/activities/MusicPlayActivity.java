package cn.lingmar.music.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.kit.handler.Run;

import butterknife.BindView;
import butterknife.OnClick;
import cn.lingmar.common.app.PresenterActivity;
import cn.lingmar.common.widget.MusicImageView;
import cn.lingmar.factory.model.Music;
import cn.lingmar.factory.presenter.MusicPlayContract;
import cn.lingmar.factory.presenter.MusicPlayPresenter;
import cn.lingmar.factory.service.MusicService;
import cn.lingmar.factory.utils.MusicUtil;
import cn.lingmar.music.R;
import cn.lingmar.music.frags.MusicListFragment;

public class MusicPlayActivity extends PresenterActivity<MusicPlayContract.Presenter>
        implements MusicPlayContract.View, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.lay_music_play)
    LinearLayout mLayout;

    @BindView(R.id.tv_title)
    TextView mTitle;

    @BindView(R.id.tv_singer)
    TextView mSinger;

    @BindView(R.id.tv_start_time)
    TextView mStartTime;

    @BindView(R.id.tv_end_time)
    TextView mEndTime;

    @BindView(R.id.iv_music_img)
    MusicImageView mMusicImg;

    @BindView(R.id.seek_bar)
    SeekBar mSeekBar;

    @BindView(R.id.iv_play)
    ImageView mPlay;

    @BindView(R.id.iv_operate)
    ImageView mOperate;

    private int position;

    // 音乐播放三种模式的图片数组
    private final int[] modeImgs = {R.drawable.ic_music_loop,
            R.drawable.ic_music_loop_1,
            R.drawable.ic_music_random_play};

    public static void show(Context context) {
        Intent intent = new Intent(context, MusicPlayActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_music_play;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        initUI();
        // 设置进度条事件
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected MusicPlayContract.Presenter initPresenter() {
        return new MusicPlayPresenter(this);
    }

    @Override
    public void onMusicResume() {
        if (animator == null) {
            startPlayAnim(mMusicImg);
            return;
        }
        animator.resume();
        mPlay.setImageResource(R.drawable.ic_music_stop);
    }

    @Override
    public void onMusicPause() {
        if (animator == null) {
            return;
        }
        animator.pause();
        mPlay.setImageResource(R.drawable.ic_music_play);
    }

    @Override
    public void onChangeProgress(int position) {
        mSeekBar.setProgress(position);
        mStartTime.setText(MusicUtil.formatTime(position));
    }

    @Override
    public void onNext() {
        initUI();
    }

    @Override
    public void onPre() {

    }

    @Override
    protected void onDestroy() {
        mPresenter.removeListener();
        super.onDestroy();
    }

    @OnClick(R.id.iv_pre)
    public void onPreClick() {
        mPresenter.doMusicPre();
    }

    @OnClick(R.id.iv_play)
    public void onPlayClick() {
        mPresenter.doMusicStartOrPause();
    }

    @OnClick(R.id.iv_next)
    public void onNextClick() {
        mPresenter.doMusicNext();
    }

    @OnClick(R.id.iv_list)
    public void onMusicListClick() {
        MusicListFragment.show(getSupportFragmentManager());
    }

    // 单曲循环、循环播放、随机播放
    @OnClick(R.id.iv_operate)
    public void onOperateClick() {
        mPresenter.doMusicChange();
        mOperate.setImageResource(modeImgs[(MusicService.getCurrentMode() + 1) % modeImgs.length]);
    }

    @OnClick(R.id.iv_back)
    public void onBack() {
        finish();
    }

    // 进度条事件
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        position = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mPresenter.seekPosition(position);
    }

    private void initUI() {
        // 初始化界面
        Music music = MusicService.getMusic();
        if (music != null) {
            mTitle.setText(music.getTitle());
            mSinger.setText(music.getArtist());
            mEndTime.setText(MusicUtil.formatTime(music.getDuration()));

            // 滑动条初始化
            mSeekBar.setMax((int) music.getDuration());
            mSeekBar.setProgress(MusicService.getCurrentPosition());

            Run.onBackground(() -> {
                Drawable drawable = MusicUtil.getForegroundDrawable(this,
                        music.getMusicCover(MusicPlayActivity.this));
                Bitmap bitmap = music.getMusicOriginalCover(this);
                Run.onUiAsync(() -> {
                    mLayout.setBackground(drawable);
                    mMusicImg.setupFit(Glide.with(this), bitmap);
                });
            });
        }

        if (!MusicService.isPlay()) {
            mPlay.setImageResource(R.drawable.ic_music_stop);
            startPlayAnim(mMusicImg);
        } else {
            mPlay.setImageResource(R.drawable.ic_music_play);
        }

        mOperate.setImageResource(modeImgs[MusicService.getCurrentMode()]);
    }
}
