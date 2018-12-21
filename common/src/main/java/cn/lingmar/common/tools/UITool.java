package cn.lingmar.common.tools;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;

/**
 * Created by Lingmar on 2017/11/13.
 */

public class UITool {
    private static int STATUS_BAR_HEIGHT = -1;

    /**
     * 得到状态栏的高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && STATUS_BAR_HEIGHT == -1) {
            try {
                final Resources res = activity.getResources();
                // 尝试获取status_bar_height这个属性Id对应的资源int值
                int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId <= 0) {
                    Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                    Object object = clazz.newInstance();
                    resourceId = Integer.parseInt(clazz.getField("status_bar_height")
                            .get(object).toString());
                }

                if (resourceId > 0) {
                    STATUS_BAR_HEIGHT = res.getDimensionPixelSize(resourceId);
                }

                if (STATUS_BAR_HEIGHT <= 0) {
                    Rect rect = new Rect();
                    Window window = activity.getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rect);
                    STATUS_BAR_HEIGHT = rect.top;
                }

            } catch (Exception e) {
                Log.e("GAV", "UITool.getStatusBarHeight");
                e.printStackTrace();
            }
        }

        return STATUS_BAR_HEIGHT;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        // activity.getWindowManager().getDefaultDisplay().getWidth();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        // activity.getWindowManager().getDefaultDisplay().getHeight();
        return displayMetrics.heightPixels;
    }
}
