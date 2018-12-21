package cn.lingmar.factory.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.HashSet;
import java.util.Set;

import cn.lingmar.factory.service.MusicService;

public class MusicProgressReceiver extends BroadcastReceiver {
    private static final MusicProgressReceiver instance;
    private final Set<ReceiverListener> listeners = new HashSet<>();

    static {
        instance = new MusicProgressReceiver();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;

        int position = intent.getIntExtra(MusicService.MUSIC_PROGRESS_POSITION, 0);
        // 回调Presenter更新界面元素
        for (ReceiverListener listener : instance.listeners) {
            listener.onProgressReceiver(position);
        }
    }

    private Set<ReceiverListener> getReceiverListener() {
        return listeners;
    }

    public static <Model> void addReceiverListener(ReceiverListener<Model> listener) {
        Set<ReceiverListener> listeners = instance.getReceiverListener();
        listeners.add(listener);
    }

    public static <Model> void removeReceiverListener(ReceiverListener<Model> listener) {
        Set<ReceiverListener> listeners = instance.getReceiverListener();
        if (listeners == null)
            return;

        listeners.remove(listener);
    }

    public interface ReceiverListener<Data> {
        void onProgressReceiver(Data data);
    }

}
