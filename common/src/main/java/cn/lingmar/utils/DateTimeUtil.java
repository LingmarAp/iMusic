package cn.lingmar.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 */
public class DateTimeUtil {
    private static final SimpleDateFormat FORMAT_HM = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    private static final SimpleDateFormat FORMAT_ZH = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    private static final SimpleDateFormat FORMAT_WEEK = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    private static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private static final long ONE_DAY = 1000 * 3600 * 24;
    private static final long TWO_DAY = 1000 * 3600 * 24 * 2;
    private static final long ONE_WEEK = 1000 * 3600 * 24 * 7;

    /**
     * 获取一个简单的时间字符串
     *
     * @param date Date
     * @return 时间字符串
     */
    public static String getSampleDate(Date date) {
        String simpleDateStr = "";
        Date nowDate = new Date();

        // 计算时间间隔
        long startTime = date.getTime();
        long endTime = nowDate.getTime();
        long intervalTime = endTime - startTime;


        if (intervalTime < ONE_DAY) // 一天之内
            simpleDateStr = FORMAT_HM.format(date);

        else if (intervalTime < TWO_DAY) // 前一天
            simpleDateStr = "昨天";

        else if (intervalTime < ONE_WEEK) // 一个星期之内
            simpleDateStr = dateToWeek(date);

        else // 一个星期之前
            simpleDateStr = FORMAT_DATE.format(date);

        return simpleDateStr;
    }

    private static String dateToWeek(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}
