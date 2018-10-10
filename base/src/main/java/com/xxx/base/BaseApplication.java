package com.xxx.base;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.xxx.base.app.BaseActivity;
import com.xxx.base.app.BaseConst;
import com.xxx.base.app.UserInfo;
import com.xxx.base.utils.AppUtils;
import com.xxx.base.utils.CodeUtils;
import com.xxx.base.utils.DevicesUtil;
import com.xxx.base.utils.FileUtils;
import com.xxx.base.utils.PackageUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * Created by niuapp on 2018/9/25 10:53.
 * Project : AndroidNA.
 * Email : 345485985@qq.com
 * -->
 */
public class BaseApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(base);
        Beta.installTinker();
    }


    //上下文
    private static Application mContext;
    //主线程的Handler
    private static Handler mMainThreadHandler;
    //主线程
    private static Thread mMainThread;
    //主线程ID
    private static int mMainThreadId;
    //Looper
    private static Looper mLooper;

    private static BaseConst mBaseConst;//一直持有常量类的实例，保证不被置null

    private static boolean initFlag = false;

    //记录打开的Activity 的集合
    private static Stack<Activity> mActivityStack;

    //记录用户信息
    private static UserInfo userInfo;
    private static final long DEFAULT_MILLISECONDS = 30000;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        AndPermission.with(this)
                .permission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int i, @NonNull List<String> list) {
                        if (!BuildConfig.DEBUG) { //不是 deBug模式才开启
                            //重写系统的异常处理器
                            Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                                @Override
                                public void uncaughtException(Thread thread, Throwable ex) {

                                    try {
                                        //出现异常时调用
                                        //把异常信息输出到sd卡对应的文件中
                                        StringBuilder sb = new StringBuilder();
                                        //时间
                                        sb.append("time: " + Formatter.formatFileSize(mContext, System.currentTimeMillis()));
                                        //编译信息
                                        Field[] fields = Build.class.getDeclaredFields();
                                        for (Field field : fields) {
                                            try {
                                                sb.append(field.getName() + " = " + field.get(null) + "\n");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        //异常信息
                                        StringWriter sw = new StringWriter();
                                        PrintWriter pw = new PrintWriter(sw);
                                        ex.printStackTrace(pw);
                                        sb.append(sw.toString());

                                        //把sb中的数据输出到 文件中
                                        //LogUtils.d("路径--> " + FileUtils.getExternalStoragePath() + "exception.info");
                                        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(FileUtils.getExternalStoragePath() + "exception.info")));
                                        bw.write(sb.toString());
                                        bw.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    //干掉自己的进程
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            });
                        } else {
                            FileUtils.writeFile("<br/><br/>", FileUtils.getExternalStoragePath() + "log.html", false);//清空记录
                        }
                    }

                    @Override
                    public void onFailed(int i, @NonNull List<String> list) {
                        if (BaseActivity.getForegroundActivity() != null) {
                            if (AndPermission.hasAlwaysDeniedPermission(BaseActivity.getForegroundActivity(), list)) {
                                AndPermission.defaultSettingDialog(BaseActivity.getForegroundActivity(), 120).show();
                            }
                        }

                    }
                })
                .start();

        if (!initFlag) {
            init();
            initFlag = true;
        }
    }

    // 初始化
    private void init() {

        initAppBaseInfo();

        initOKGO();

        initUserInfo();//用户信息初始化

        initBugly();
    }


    /**
     * 获取已经打开的Activity集合
     *
     * @return 已经打开的Activity集合
     */
    public static List<Activity> getActivityList() {
        return mActivityStack;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息对象
     */
    public synchronized static UserInfo getUserInfo() {
        return BaseApplication.userInfo;
    }


    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }

    public static Looper getLooper() {
        return mLooper;
    }


    private void initBugly() {
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = PackageUtils.getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        Bugly.setIsDevelopmentDevice(getApplicationContext(), DevicesUtil.isDevelopmentDevice());
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            @Override
            public synchronized Map<String, String> onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {
                HashMap<String, String> map = new HashMap<String, String>();
                //崩溃上报的头
                map.put("isRoot", String.valueOf(DevicesUtil.isRooted()));
                map.put("isEmulator", String.valueOf(DevicesUtil.isEmulator()));
                map.put("userId", getUserInfo().getSESSION_KEY());
                return map;
            }
        });
        CrashReport.setUserId(getUserInfo().getSESSION_KEY());
        Bugly.init(getApplicationContext(), "b244b7cb90", BuildConfig.DEBUG, strategy);
        CrashReport.initCrashReport(mContext, strategy);
        CrashReport.setAppChannel(mContext, AppUtils.getChannel());
    }


    /**
     * 初始化
     */
    private void initUserInfo() {
        BaseApplication.userInfo = new UserInfo();

        //试加载缓存
        //查看是否有缓存Json 试解析
        String cache_userInfo = FileUtils.getSharedPreferences().getString(BaseConst.getInstance().CACHE_USERINFO, "");
        if (!TextUtils.isEmpty(cache_userInfo)) {
            //得到数据后更新用户信息
            try {
                userInfo = new Gson().fromJson(CodeUtils.decodeByBase64(cache_userInfo), UserInfo.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 基本内容
     */
    private void initAppBaseInfo() {
        mMainThreadHandler = new Handler();
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();
        mLooper = getMainLooper();

        mBaseConst = BaseConst.getInstance();//
    }

    /**
     * okgo
     */
    private void initOKGO() {
        //初始OkGo
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //请求log
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
            //log打印级别，决定了log显示的详细程度
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            //log颜色级别，决定了log在控制台显示的颜色
            loggingInterceptor.setColorLevel(Level.INFO);

            builder.addInterceptor(loggingInterceptor);
        }

        //全局的读取超时时间
        builder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        //信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);


        OkGo.getInstance().init(mContext)
                .setOkHttpClient(builder.build())
                .setCacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .setRetryCount(3);
    }

    public static Application getContext() {
        return mContext;
    }


    /**
     * 添加Activity到堆栈
     */
    public static void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<Activity>();
        }
        mActivityStack.add(activity);
    }

    /**
     * 移除Activity
     */
    public static void removeActivity(Activity activity) {
        if (mActivityStack == null) {
            return;
        }
        mActivityStack.remove(activity);
    }

    public static Stack<Activity> getActivityStack() {
        return mActivityStack;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    @Nullable
    public static Activity currentActivity() {
        if (mActivityStack == null) return null;
        if (mActivityStack.isEmpty()) {
            return null;
        } else {
            return mActivityStack.lastElement();
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public static void finishActivity() {
        if (mActivityStack == null) return;
        if (!mActivityStack.isEmpty()) {
            finishActivity(mActivityStack.lastElement());
        }
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (mActivityStack == null) return;
        if (activity != null) {
//            mActivityStack.iterator().remove();
            mActivityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        if (mActivityStack == null) return;
        for (int i = 0; i < mActivityStack.size(); i++) {
            Activity activity = mActivityStack.get(i);
            if (activity != null && activity.getClass() == cls) {
                if (!activity.isFinishing()) {
                    activity.finish();
                }
                mActivityStack.remove(activity);
                i--;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        if (mActivityStack == null) return;
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                Activity activity = mActivityStack.get(i);
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
        mActivityStack.clear();
    }

    /**
     * 结束所有Activity 除了当前Activity
     *
     * @param activity 指定的对象
     * @param skipList 跳过的类
     */
    public static void finishAllActivity(Activity activity, Class... skipList) {
        if (mActivityStack == null) return;
        mainFor:
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            Activity finishActivity = mActivityStack.get(i);
            if (null != finishActivity && activity != finishActivity) {
                if (skipList != null) {
                    for (int j = 0; j < skipList.length; j++) {
                        if (finishActivity.getClass() == skipList[j]) {
                            continue mainFor;//跳过所属该集合的activity 不关闭
                        }
                    }
                }
                if (!finishActivity.isFinishing()) {
                    finishActivity.finish();
                }
            }
        }
    }
}
