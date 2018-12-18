package cn.lingmar.factory;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import cn.lingmar.common.app.Application;
import cn.lingmar.factory.data.helper.MusicHelper;
import cn.lingmar.factory.data.music.MusicCenter;
import cn.lingmar.factory.data.music.MusicDispatcher;

public class Factory {

    /**
     * Factory中的初始化
     */
    public static void setup() {
        // 初始化数据库
        FlowManager.init(new FlowConfig.Builder(Application.getInstance())
                .openDatabasesOnInit(true)  // 数据库初始化的时候就开始打开
                .build());

        // TODO 持久化的数据进行初始化
    }

    public static MusicCenter getMusicCenter() {
        return MusicDispatcher.instance();
    }

    public static MusicHelper getMusicHelper() {
        return MusicHelper.instance();
    }
}
