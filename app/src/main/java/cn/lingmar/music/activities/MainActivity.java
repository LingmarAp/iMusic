package cn.lingmar.music.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.ui.Ui;

import butterknife.BindView;
import butterknife.OnClick;
import cn.lingmar.common.app.Application;
import cn.lingmar.common.app.PresenterListActivity;
import cn.lingmar.common.widget.MusicImageView;
import cn.lingmar.common.widget.MusicItemDecoration;
import cn.lingmar.common.widget.recycler.RecyclerAdapter;
import cn.lingmar.factory.model.Music;
import cn.lingmar.factory.presenter.MusicContract;
import cn.lingmar.factory.presenter.MusicPresenter;
import cn.lingmar.music.R;

public class MainActivity extends PresenterListActivity<MusicContract.Presenter>
        implements MusicContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.refresh)
    SwipeRefreshLayout mRefresh;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    @BindView(R.id.tv_title)
    TextView mTitle;

    @BindView(R.id.tv_content)
    TextView mContent;

    @BindView(R.id.iv_play)
    ImageView mPlay;

    @BindView(R.id.iv_music_img)
    MusicImageView mMusicImg;

    private RecyclerAdapter<Music> mAdapter;
    private ObjectAnimator animator;
    private int index = 0; // 播放音乐的序号

    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 初始化Recycler
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        // 添加分割线
        mRecycler.addItemDecoration(new MusicItemDecoration(this,
                MusicItemDecoration.VERTICAL_LIST,
                R.drawable.shap_music_item,
                (int) Ui.dipToPx(getResources(), 32)));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Music>() {

            @Override
            protected int getItemViewType(int position, Music music) {
                return R.layout.cell_music_list;
            }

            @Override
            protected ViewHolder<Music> onCreateViewHolder(View root, int viewType) {
                return new MainActivity.ViewHolder(root);
            }
        });

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Music>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Music music) {
                super.onItemClick(holder, music);
                // 播放歌曲
                mPresenter.doMusicPlay(music, index = holder.getAdapterPosition());
                // TODO 更改Item为播放时的颜色
            }
        });

        // 初始化Refresh事件
        mRefresh.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @Override
    protected MusicContract.Presenter initPresenter() {
        return new MusicPresenter(this);
    }

    @Override
    protected SwipeRefreshLayout initRefresh() {
        return mRefresh;
    }

    @Override
    public RecyclerAdapter<Music> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        hideLoading();
        Application.showToast(cn.lingmar.lang.R.string.data_music_refresh_done);
    }

    @Override
    public void onMusicPlay(Music music) {
        startPlayAnim();
        // 改变播放时显示的信息
        mTitle.setText(music.getTitle());
        mContent.setText(music.getDescription());
        mMusicImg.setup(Glide.with(this), music.getMusicCover());
        mPlay.setImageResource(R.drawable.ic_music_stop);
    }

    @Override
    public void onMusicResume() {
        mPlay.setImageResource(R.drawable.ic_music_stop);
        if (animator == null) {
            startPlayAnim();
            return;
        }
        animator.resume();
    }

    @Override
    public void onMusicPause() {
        mPlay.setImageResource(R.drawable.ic_music_play);
        if (animator == null) {
            return;
        }
        animator.pause();
    }

    // SwipeRefreshLayout的刷新事件
    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @OnClick(R.id.iv_play)
    public void onPlayClick() {
        mPresenter.doMusicStartOrPause();
    }

    // 开始音乐播放动画
    private void startPlayAnim() {
        if (animator != null)
            animator.cancel();

        animator = ObjectAnimator.ofFloat(mMusicImg, "rotation", 0f, 360f);
        animator.setDuration(6000);
        animator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.start();
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<Music> {
        @BindView(R.id.view_flag)
        View mFlag;

        @BindView(R.id.tv_title)
        TextView mTitle;

        @BindView(R.id.tv_singer)
        TextView mSinger;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Music music) {
            mTitle.setText(music.getTitle());
            mSinger.setText(music.getDescription());
        }

        @OnClick(R.id.iv_more)
        void onMoreClick() {
        }
    }
}
