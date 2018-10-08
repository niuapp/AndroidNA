package com.xxx.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.xxx.base.BaseApplication;
import com.xxx.base.R;
import com.xxx.base.app.BaseActivity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class AppUtils {

    private static TextView toastTextView;

    /**
     * 获取 全局Context
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        return BaseApplication.getContext();
    }

    public static Thread getMainThread() {
        return BaseApplication.getMainThread();
    }

    public static long getMainThreadId() {
        return BaseApplication.getMainThreadId();
    }

    /**
     * 获取Activity记录集合
     *
     * @return
     */
    public static List<Activity> getActivityList() {
        return BaseApplication.getActivityList();
    }

    /**
     * dip -> px
     */
    public static int dip2Px(int dp) {
        //获取屏幕显示规格密度
        float scale = getContext().getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5);
        return px;
    }

    /**
     * px -> dip
     */
    public static int px2Dip(int px) {
        //获取屏幕显示规格密度
        float scale = getContext().getResources().getDisplayMetrics().density;
        int dp = (int) (px / scale + 0.5);
        return dp;
    }

    /**
     * 获取最大宽度
     *
     * @return
     */
    public static int getMaxWidth() {

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取最大高度
     *
     * @return
     */
    public static int getMaxHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 获取主线程的handler
     */
    public static Handler getHandler() {
        return BaseApplication.getMainThreadHandler();
    }

    /**
     * 延时在主线程执行runnable
     */
    public static boolean postDelayed(Runnable runnable, long delayMillis) {
        return getHandler().postDelayed(runnable, delayMillis);
    }

    /**
     * 在主线程执行runnable
     */
    public static boolean post(Runnable runnable) {
        return getHandler().post(runnable);
    }

    /**
     * 在新线程执行runnable
     */
    public static void postOnNewThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * 从主线程looper里面移除runnable
     */
    public static void removeCallbacks(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    /**
     * @return 获取App 当前渠道 , 不同于 BuildConfig.FLAVOR 它读取的是 manifest 文件里面的 mate-data ,
     * 并且只有 TD_CHANNEL_ID 和 UMENG_CHANNEL 相同才会返回有意义的值
     * 避免了应用经过第三方工具多渠道打包之后读取不准确 ( 使用 BuildConfig.FLAVOR 第三方工具不行)
     */
    public static String getChannel() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = getContext().getPackageManager()
                    .getApplicationInfo(getContext().getPackageName(),
                            PackageManager.GET_META_DATA);
            if (appInfo == null) return "";
            String td_channel_id = appInfo.metaData.getString("TD_CHANNEL_ID");
            String umeng_channel = appInfo.metaData.getString("UMENG_CHANNEL");
            if (!TextUtils.isEmpty(td_channel_id) && !TextUtils.isEmpty(umeng_channel) && TextUtils.equals(td_channel_id, umeng_channel)) {
                return td_channel_id;
            } else {
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 通过id创建View
     *
     * @param resId
     * @return
     */
    public static <T extends View> T inflate(int resId) {
        return (T) inflate(getContext(), resId);
    }

    public static <T extends View> T inflate(Context context, int resId) {
        return (T) View.inflate(context, resId, null);
    }

    /**
     * 通过名字获取资源中的字符串
     * @param fieldName
     * @return
     */
    public static String getStringFromResource(String fieldName) {
        int stringID = getContext().getResources().getIdentifier(fieldName, "string", getContext().getPackageName());

        return getResources().getString(stringID);
    }

    /**
     * 获取资源
     */
    public static Resources getResources() {
//        System.out.println(".......haha" + getContext() == null ? true : false);
        return getContext().getResources();
    }

    /**
     * 获取文字
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 获取文字数组
     */
    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 获取dimen
     */
    public static int getDimens(int resId) {
        return getResources().getDimensionPixelOffset(resId);
    }

    /**
     * 获取drawable
     */
    public static Drawable getDrawable(int resId) {
        return getResources().getDrawable(resId);
    }

    /**
     * 把毫秒值 转换成 分秒
     *
     * @param value
     * @return
     */
    public static String formatDate(long value, String type) {
        SimpleDateFormat formatter = null;
        String date = "";
        switch (type) {
            case "ms":
                formatter = new SimpleDateFormat("mm:ss");
                try {
                    date = formatter.format(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "hm":
                try {
                    formatter = new SimpleDateFormat("hh:mm");
                    date = formatter.format(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            default:
                break;
        }

        return date;

    }

    /**
     * 毫秒值转自定义格式时间（如 pattern = "yyyy-MM-dd"）
     *
     * @param pattern
     * @param dateTime
     * @return
     */
    public static String getFormatedDateTime(String pattern, long dateTime) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(new Date(dateTime + 0));
    }


    /**
     * 获取颜色
     */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    /**
     * 获取颜色选择器
     */
    public static ColorStateList getColorStateList(int resId) {
        return getResources().getColorStateList(resId);
    }

    /**
     * 判断当前的线程是不是在主线程
     */
    public static boolean isRunInMainThread() {
        return android.os.Process.myTid() == getMainThreadId();
    }

    /**
     * 在主线程中执行
     *
     * @param runnable
     */
    public static void runInMainThread(Runnable runnable) {
        if (isRunInMainThread()) {
            runnable.run();
        } else {
            post(runnable);
        }
    }


    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     */
    public static void showToastSafe(@StringRes final int resId) {
        showToastSafe(getString(resId));
    }

    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     */
    public static void showToastSafe(final String str) {
        if (isRunInMainThread()) {
            showToast(str);
//            showToast_dfui(str);//更改样式的  顶部 红底
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showToast(str);
                }
            });
        }
    }

    private static Toast myToast;

    /**
     * 提示 正常
     *
     * @param str
     */
    private static void showToast(String str) {
        if (myToast == null) {
            myToast = Toast.makeText(getContext(), str, Toast.LENGTH_SHORT);
        } else {
            myToast.setText(str);
        }
        myToast.show();
    }

//    /**
//     * 提示 更改ui
//     *
//     * @param str
//     */
//    private static void showToast_dfui(String str) {
//
//        if (myToast == null || toastTextView == null) {
//            myToast = new Toast(getContext());
//            myToast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, -1000);
//            myToast.setDuration(Toast.LENGTH_SHORT);
//            toastTextView = (TextView) inflate(R.layout.toastview);
//            toastTextView.setText(str.trim());
//            myToast.setView(toastTextView);
//        } else {
//            toastTextView.setText(str.trim());
//            myToast.setView(toastTextView);
//        }
//        myToast.show();
//    }

    /**
     * 隐藏Toast
     */
    public static void hintToast() {
        if (myToast != null) {
            myToast.cancel();
        }
    }

    /**
     * 返回应用中当前显示的Activity
     *
     * @return
     */
    public static BaseActivity getForegroundActivity() {
        return BaseActivity.getForegroundActivity();
    }

//    /**
//     * 在当前Activity上弹出窗口，通过传入view 和 windowManager 弹出，在外界控制显示
//     *
//     * @param windowManager 当前Activity对应的 windowManager
//     * @param contentView   当前窗口要显示的内容
//     */
//    public static void showWindow(WindowManager windowManager, View contentView) {
//
//        // 设置布局参数
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//        //宽高填充
//        params.height = WindowManager.LayoutParams.MATCH_PARENT;
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        //全屏 要有WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，好像因为这个才能相应返回键？
//        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        //透明窗体
//        params.format = PixelFormat.TRANSPARENT;
//        //普通应用程序窗口
//        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
//        //添加
//        windowManager.addView(contentView, params);
//    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 获取指定范围随机数 包左包右
     *
     * @param start
     * @param end
     * @return
     */
    public static int getRandom(int start, int end) {
        return (int) (Math.random() * (end - start + 1)) + start;
    }

    /**
     * 给多个View设置点击监听，switch id 判断
     *
     * @param onClickListener 点击监听
     * @param views
     */
    public static void setClickListener(View.OnClickListener onClickListener, View... views) {
        if (views != null) {
            for (View view : views) {
                if (view != null) {
                    view.setOnClickListener(onClickListener);
                }
            }
        }
    }


    /**
     * ￥ 小数 字体变小
     *
     * @param price
     * @param pricePrefixEnable 前后 ￥小数 是否变小
     * @return
     */
    public static SpannableString formatPricep(String price, boolean pricePrefixEnable) {
        double parseDouble = 0;
        try {
            parseDouble = Double.parseDouble(price);
            if (parseDouble <= 0) {
                parseDouble = 0;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        price = decimalFormat.format(parseDouble);

//        LogUtils.d(price);

        price = price.substring(0, price.indexOf(".") + 3);


        String str = "￥" + (TextUtils.isEmpty(price) ? "0.00" : price);
        SpannableString spannableString = new SpannableString(str);

        if (pricePrefixEnable) {
            spannableString.setSpan(new RelativeSizeSpan(0.6f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.6f), str.indexOf("."), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }

    /**
     * ￥小数 是否变小
     *
     * @param price
     * @return
     */
    public static SpannableString formatPricep(String price) {
        price = StringUtils.checkStr_empty(price);
        return formatPricep(price, true);
    }


    public static SpannableString parseText(CharSequence text, int startPosition, int endPosition, int resID) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(resID)), startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }


    /**
     * 校验动画
     *
     * @param checkedResult
     * @param animView
     */
    public static boolean checkedAnim(boolean checkedResult, View animView) {
        if (!checkedResult) {
            animView.startAnimation(AnimationUtils.loadAnimation(AppUtils.getContext(), R.anim.shake_anim));
        }
        return checkedResult;
    }


    /**
     * findViewById
     *
     * @param contentView
     * @param resId
     * @param <F>
     * @return
     */
    public static <F extends View> F $(View contentView, int resId) {
        return (F) contentView.findViewById(resId);
    }

    /**
     * 设置文字
     *
     * @param parentView
     * @param resId
     * @param text
     */
    public static void setText(View parentView, @IdRes int resId, String text) {
        TextView textView = $(parentView, resId);
        textView.setText(text);
    }

    public static void setText(View parentView, @IdRes int resId, Spannable text) {
        TextView textView = $(parentView, resId);
        textView.setText(text);
    }

    /**
     * 把一个时间字符串解析成 Date
     * <p>
     * yyyy-MM-dd HH:mm:ss
     *
     * @param dateStr
     * @return
     */
    public static Date parseStr2Date(String dateStr, String pattern) {
        Date date = null;

        try {
            if (TextUtils.isEmpty(pattern)) {
                pattern = "yyyy-MM-dd HH:mm:ss";
            }

            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();

            if (!TextUtils.isEmpty(dateStr)) {
                simpleDateFormat.applyPattern(pattern);
                date = simpleDateFormat.parse(dateStr);
            } else {
                date = new Date();
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) {
            date = new Date();
        }

        return date;
    }

    /**
     * 把一个date对象 解析成对应日期字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String parseDate2Str(Date date, String pattern) {

        String dateStr = "";
        try {

            if (date == null) {
                date = new Date();
            }

            if (TextUtils.isEmpty(pattern)) {
                pattern = "yyyy-MM-dd HH:mm:ss";
            }
            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();

            simpleDateFormat.applyPattern(pattern);
            dateStr = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateStr;
    }

    /**
     * 解析一个毫秒值 ->  时 : 分 : 秒
     *
     * @return
     */
    public static String parseDateMS2Str_hms(long ms) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        //转换时分秒
        ms = ms / 1000;
        int h = (int) (ms / 3600);
        int m = (int) ((ms - (h * 3600)) / 60);
        int s = (int) (ms - (h * 3600) - (m * 60));

        return decimalFormat.format(h) + " : " + decimalFormat.format(m) + " : " + decimalFormat.format(s);
    }

    /**
     * 毫秒值 变成估算的 年
     *
     * @param ms
     * @return
     */
    public static String parseDateMS2Str_y(long ms) {
        return (ms / 1000 / 3600 / 24 / 365) + "";
    }

    /**
     * @param starTime
     * @param endTime
     * @return
     */
    public static String getTimeDifference(String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();

            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);
            long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            long ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                    - min * 60 * 1000 - s * 1000);
            // System.out.println(day + "天" + hour + "小时" + min + "分" + s +
            // "秒");
            long hour1 = diff / (60 * 60 * 1000);
            String hourString = hour1 + "";
            long min1 = ((diff / (60 * 1000)) - hour1 * 60);
            timeString = hour1 + "小时" + min1 + "分";
            // System.out.println(day + "天" + hour + "小时" + min + "分" + s +
            // "秒");

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeString;

    }

}
