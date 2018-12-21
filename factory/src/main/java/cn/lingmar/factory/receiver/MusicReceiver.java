package cn.lingmar.factory.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.HashSet;
import java.util.Set;

public class MusicReceiver extends BroadcastReceiver {
    private static final MusicReceiver instance;
    private final Set<ReceiverListener> listeners = new HashSet<>();

    static {
        instance = new MusicReceiver();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;

        // TODO 回调Presenter更新界面元素
        for (ReceiverListener listener : instance.listeners) {
            listener.onDataReceiver();
        }
    }

    private Set<ReceiverListener> getReceiverListener() {
        return listeners;
    }

    public static void addReceiverListener(ReceiverListener listener) {
        Set<ReceiverListener> listeners = instance.getReceiverListener();
        listeners.add(listener);
    }

    public static void removeReceiverListener(ReceiverListener listener) {
        Set<ReceiverListener> listeners = instance.getReceiverListener();
        if (listeners == null)
            return;

        listeners.remove(listener);
    }

    public interface ReceiverListener {
        void onDataReceiver();
    }

}
