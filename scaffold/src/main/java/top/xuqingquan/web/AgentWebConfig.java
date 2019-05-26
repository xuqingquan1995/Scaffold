package top.xuqingquan.web;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.Nullable;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;
import top.xuqingquan.utils.Timber;

import java.io.File;

public class AgentWebConfig {

    static final String FILE_CACHE_PATH = "scaffold-cache";
    private static final String AGENTWEB_CACHE_PATCH = File.separator + "scaffold-cache";
    /**
     * 缓存路径
     */
    static String AGENTWEB_FILE_PATH;
    /**
     * DEBUG 模式 ， 如果需要查看日志请设置为 true
     */
    public static boolean DEBUG = false;
    /**
     * 当前操作系统是否低于 KITKAT
     */
    static final boolean IS_KITKAT_OR_BELOW_KITKAT = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
    /**
     * 默认 WebView  类型 。
     */
    static final int WEBVIEW_DEFAULT_TYPE = 1;
    /**
     * 使用 AgentWebView
     */
    static final int WEBVIEW_AGENTWEB_SAFE_TYPE = 2;
    /**
     * 自定义 WebView
     */
    static final int WEBVIEW_CUSTOM_TYPE = 3;
    static int WEBVIEW_TYPE = WEBVIEW_DEFAULT_TYPE;
    private static volatile boolean IS_INITIALIZED = false;
    /**
     * AgentWeb 的版本
     */
    static final String AGENTWEB_VERSION = " agentweb/4.0.2 ";
    /**
     * 通过JS获取的文件大小， 这里限制最大为5MB ，太大会抛出 OutOfMemoryError
     */
    static int MAX_FILE_LENGTH = 1024 * 1024 * 5;

    //获取Cookie
    static String getCookiesByUrl(String url) {
        return CookieManager.getInstance() == null ? null : CookieManager.getInstance().getCookie(url);
    }

    public static void debug() {
        DEBUG = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    /**
     * @param context
     * @return WebView 的缓存路径
     */
    static String getCachePath(Context context) {
        return context.getCacheDir().getAbsolutePath() + AGENTWEB_CACHE_PATCH;
    }

    //Android  4.4  NoSuchMethodError: android.webkit.CookieManager.removeAllCookies
    static void removeAllCookies(@Nullable ValueCallback<Boolean> callback) {
        if (callback == null) {
            callback = getDefaultIgnoreCallback();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookie();
            toSyncCookies();
            callback.onReceiveValue(!CookieManager.getInstance().hasCookies());
            return;
        }
        CookieManager.getInstance().removeAllCookies(callback);
        toSyncCookies();
    }

    static synchronized void initCookiesManager(Context context) {
        if (!IS_INITIALIZED) {
            createCookiesSyncInstance(context);
            IS_INITIALIZED = true;
        }
    }

    private static void createCookiesSyncInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
    }

    private static void toSyncCookies() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
            return;
        }
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> CookieManager.getInstance().flush());
    }

    private static ValueCallback<Boolean> getDefaultIgnoreCallback() {
        return ignore -> Timber.i("removeExpiredCookies:" + ignore);
    }
}
