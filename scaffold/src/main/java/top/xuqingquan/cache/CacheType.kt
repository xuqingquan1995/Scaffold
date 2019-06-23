package top.xuqingquan.cache

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import androidx.fragment.app.Fragment
import top.xuqingquan.integration.RepositoryManager

/**
 * Created by 许清泉 on 2019/4/14 16:52
 */
interface CacheType {

    /**
     * 返回框架内需要缓存的模块对应的 `id`
     *
     * @return
     */
    val cacheTypeId: Int

    /**
     * 计算对应模块需要的缓存大小
     *
     * @return
     */
    fun calculateCacheSize(context: Context): Int

    companion object {
        const val RETROFIT_SERVICE_CACHE_TYPE_ID = 0
        const val EXTRAS_TYPE_ID = 1
        const val ACTIVITY_CACHE_TYPE_ID = 2
        const val FRAGMENT_CACHE_TYPE_ID = 3

        /**
         * [RepositoryManager]中存储 Retrofit Service 的容器
         */
        @JvmStatic
        val RETROFIT_SERVICE_CACHE: CacheType = object : CacheType {
            private val MAX_SIZE = 150
            private val MAX_SIZE_MULTIPLIER = 0.002f

            override val cacheTypeId: Int
                get() = RETROFIT_SERVICE_CACHE_TYPE_ID

            override fun calculateCacheSize(context: Context): Int {
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val targetMemoryCacheSize =
                    (activityManager.memoryClass.toFloat() * MAX_SIZE_MULTIPLIER * 1024f).toInt()
                return if (targetMemoryCacheSize >= MAX_SIZE) {
                    MAX_SIZE
                } else targetMemoryCacheSize
            }
        }

        @JvmStatic
        val EXTRAS: CacheType = object : CacheType {
            private val MAX_SIZE = 500
            private val MAX_SIZE_MULTIPLIER = 0.005f

            override val cacheTypeId: Int
                get() = EXTRAS_TYPE_ID

            override fun calculateCacheSize(context: Context): Int {
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val targetMemoryCacheSize =
                    (activityManager.memoryClass.toFloat() * MAX_SIZE_MULTIPLIER * 1024f).toInt()
                return if (targetMemoryCacheSize >= MAX_SIZE) {
                    MAX_SIZE
                } else targetMemoryCacheSize
            }
        }

        /**
         * [Activity] 中存储数据的容器
         */
        @JvmStatic
        val ACTIVITY_CACHE: CacheType = object : CacheType {
            private val MAX_SIZE = 80
            private val MAX_SIZE_MULTIPLIER = 0.0008f

            override val cacheTypeId: Int
                get() = ACTIVITY_CACHE_TYPE_ID

            override fun calculateCacheSize(context: Context): Int {
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val targetMemoryCacheSize =
                    (activityManager.memoryClass.toFloat() * MAX_SIZE_MULTIPLIER * 1024f).toInt()
                return if (targetMemoryCacheSize >= MAX_SIZE) {
                    MAX_SIZE
                } else targetMemoryCacheSize
            }
        }

        /**
         * [Fragment] 中存储数据的容器
         */
        @JvmStatic
        val FRAGMENT_CACHE: CacheType = object : CacheType {
            private val MAX_SIZE = 80
            private val MAX_SIZE_MULTIPLIER = 0.0008f

            override val cacheTypeId: Int
                get() = FRAGMENT_CACHE_TYPE_ID

            override fun calculateCacheSize(context: Context): Int {
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val targetMemoryCacheSize =
                    (activityManager.memoryClass.toFloat() * MAX_SIZE_MULTIPLIER * 1024f).toInt()
                return if (targetMemoryCacheSize >= MAX_SIZE) {
                    MAX_SIZE
                } else targetMemoryCacheSize
            }
        }
    }
}
