package cn.lingmar.common.app;

import android.support.annotation.StringRes;
import android.widget.Toast;

import net.qiujuer.genius.kit.handler.Run;

public class Application extends android.app.Application {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 外部获取单例
     *
     * @return 这个类的单例对象
     */
    public static Application getInstance() {
        return instance;
    }

    public static void showToast(final String msg) {
        // Toast 只能在主线程中显示，需要进行线程转换
        Run.onUiAsync(() -> Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show());
    }

    public static void showToast(@StringRes int msgId) {
        showToast(instance.getString(msgId));
    }

}
