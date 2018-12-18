package cn.lingmar.music;


import cn.lingmar.common.app.Application;
import cn.lingmar.factory.Factory;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化操作
        Factory.setup();
    }
}
