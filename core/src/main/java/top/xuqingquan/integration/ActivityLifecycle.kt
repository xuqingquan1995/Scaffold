package top.xuqingquan.integration

import android.app.Activity
import android.app.Application
import android.os.Bundle
import top.xuqingquan.utils.Preconditions
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import top.xuqingquan.app.ScaffoldConfig
import top.xuqingquan.cache.Cache
import top.xuqingquan.cache.IntelligentCache
import top.xuqingquan.delegate.ActivityDelegate
import top.xuqingquan.delegate.ActivityDelegateImpl
import top.xuqingquan.delegate.IActivity

/**
 * Created by 许清泉 on 2019/4/14 15:24
 * [Application.ActivityLifecycleCallbacks]  默认实现类
 * 通过 [ActivityDelegate] 管理 [Activity]
 */
internal class ActivityLifecycle : Application.ActivityLifecycleCallbacks {

    private var mAppManager = ScaffoldConfig.getAppManager()
    private var mApplication = ScaffoldConfig.getApplication()
    private var mExtras = ScaffoldConfig.getExtras()
    private var mFragmentLifecycle: FragmentManager.FragmentLifecycleCallbacks =
        ScaffoldConfig.getFragmentLifecycleCallbacks()
    private var mFragmentLifecycles: MutableList<FragmentManager.FragmentLifecycleCallbacks> =
        ScaffoldConfig.getFragmentLifecycleCallbacksList()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mAppManager.addActivity(activity)
        //配置ActivityDelegate
        if (activity is IActivity) {
            var activityDelegate = fetchActivityDelegate(activity)
            if (activityDelegate == null) {
                val cache = getCacheFromActivity(activity as IActivity)
                activityDelegate = ActivityDelegateImpl(activity)
                //使用 IntelligentCache.KEY_KEEP 作为 key 的前缀, 可以使储存的数据永久存储在内存中
                //否则存储在 LRU 算法的存储空间中, 前提是 Activity 使用的是 IntelligentCache (框架默认使用)
                cache.put(IntelligentCache.getKeyOfKeep(ActivityDelegate.ACTIVITY_DELEGATE), activityDelegate)
            }
            activityDelegate.onCreate(activity,savedInstanceState)
        }
        registerFragmentCallbacks(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        val activityDelegate = fetchActivityDelegate(activity)
        activityDelegate?.onStart(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        mAppManager.setCurrentActivity(activity)
        val activityDelegate = fetchActivityDelegate(activity)
        activityDelegate?.onResume(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        val activityDelegate = fetchActivityDelegate(activity)
        activityDelegate?.onPause(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        if (mAppManager.getCurrentActivity() === activity) {
            mAppManager.setCurrentActivity(null)
        }
        val activityDelegate = fetchActivityDelegate(activity)
        activityDelegate?.onStop(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        val activityDelegate = fetchActivityDelegate(activity)
        activityDelegate?.onSaveInstanceState(activity,outState)
    }

    override fun onActivityDestroyed(activity: Activity) {
        mAppManager.removeActivity(activity)
        val activityDelegate = fetchActivityDelegate(activity)
        if (activityDelegate != null) {
            activityDelegate.onDestroy(activity)
            getCacheFromActivity(activity as IActivity).clear()
        }
    }

    private fun registerFragmentCallbacks(activity: Activity) {
        if (activity is FragmentActivity) {
            //mFragmentLifecycle 为 Fragment 生命周期实现类, 用于框架内部对每个 Fragment 的必要操作, 如给每个 Fragment 配置 FragmentDelegate
            //注册框架内部已实现的 Fragment 生命周期逻辑
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(mFragmentLifecycle, true)
            if (mExtras.containsKey(IntelligentCache.getKeyOfKeep(LifecycleConfig::class.java.name))) {
                @Suppress("UNCHECKED_CAST")
                val modules =
                    mExtras.get(IntelligentCache.getKeyOfKeep(LifecycleConfig::class.java.name)) as List<LifecycleConfig>?
                modules?.forEach {
                    it.injectFragmentLifecycle(mApplication, mFragmentLifecycles)
                }
                mExtras.remove(IntelligentCache.getKeyOfKeep(LifecycleConfig::class.java.name))
            }

            //注册框架外部, 开发者扩展的 Fragment 生命周期逻辑
            for (fragmentLifecycle in mFragmentLifecycles) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycle, true)
            }
        }
    }

    private fun fetchActivityDelegate(activity: Activity): ActivityDelegate? {
        var activityDelegate: ActivityDelegate? = null
        if (activity is IActivity) {
            val cache = getCacheFromActivity(activity as IActivity)
            activityDelegate =
                cache.get(IntelligentCache.getKeyOfKeep(ActivityDelegate.ACTIVITY_DELEGATE)) as ActivityDelegate?
        }
        return activityDelegate
    }

    private fun getCacheFromActivity(activity: IActivity): Cache<String, Any> {
        val cache = activity.provideCache()
        Preconditions.checkNotNull(cache, Cache::class.java.name + " cannot be null on Activity")
        return cache
    }
}
