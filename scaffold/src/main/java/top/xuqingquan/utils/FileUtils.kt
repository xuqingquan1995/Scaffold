package top.xuqingquan.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.format.DateUtils
import androidx.core.content.FileProvider
import java.io.Closeable
import java.io.File
import java.util.*

/**
 * Created by 许清泉 on 2019/4/14 21:49
 */
object FileUtils {

    /**
     * 创建未存在的文件夹
     *
     * @param file
     * @return
     */
    @JvmStatic
    fun makeDirs(file: File): File {
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    /**
     * 返回缓存文件夹
     */
    @JvmStatic
    fun getCacheFile(context: Context): File {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            var file: File?
            file = context.externalCacheDir//获取系统管理的sd卡缓存文件
            if (file == null) {//如果获取的文件为空,就使用自己定义的缓存文件夹做缓存路径
                file = File(getCacheFilePath(context))
                makeDirs(file)
            }
            file
        } else {
            context.cacheDir
        }
    }

    /**
     * 获取自定义缓存文件地址
     *
     * @param context
     * @return
     */
    fun getCacheFilePath(context: Context): String {
        val packageName = context.packageName
        return "${Environment.getExternalStorageDirectory().path}/$packageName"
    }

    @JvmStatic
    fun getMIMEType(f: File): String {
        val type: String
        val fName = f.name
        /* 取得扩展名 */
        val end = fName.substring(fName.lastIndexOf(".") + 1).toLowerCase()
        /* 依扩展名的类型决定MimeType */
        type = when (end) {
            "pdf" -> "application/pdf"//
            "m4a", "mp3", "mid", "xmf", "ogg", "wav" -> "audio/*"
            "3gp", "mp4" -> "video/*"
            "jpg", "gif", "png", "jpeg", "bmp" -> "image/*"
            "apk" -> "application/vnd.android.package-archive"
            "pptx", "ppt" -> "application/vnd.ms-powerpoint"
            "docx", "doc" -> "application/vnd.ms-word"
            "xlsx", "xls" -> "application/vnd.ms-excel"
            else -> "*/*"
        }
        return type
    }

    @JvmStatic
    fun getUriFromFile(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getUriFromFileForN(context, file)
        } else {
            Uri.fromFile(file)
        }
    }

    private fun getUriFromFileForN(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context, context.packageName + ".ScaffoldFileProvider", file)
    }

    /**
     * 删除多久没有修改过的文件
     */
    @JvmStatic
    fun clearCacheFolder(dir: File?, numDays: Int): Int {
        var deletedFiles = 0
        if (dir != null) {
            Timber.i("dir:" + dir.absolutePath)
        }
        if (dir != null && dir.isDirectory) {
            try {
                for (child in dir.listFiles()) {
                    //first delete subdirectories recursively
                    if (child.isDirectory) {
                        deletedFiles += clearCacheFolder(child, numDays)
                    }
                    //then delete the files and subdirectories in this dir
                    //only empty directories can be deleted, so subdirs have been done first
                    if (child.lastModified() < Date().time - numDays * DateUtils.DAY_IN_MILLIS) {
                        Timber.i("file name:" + child.name)
                        if (child.delete()) {
                            deletedFiles++
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to clean the cache, result %s", e.message)
            }

        }
        return deletedFiles
    }


    /**
     * 将一系列uri转为String的路径
     */
    @JvmStatic
    fun uriToPath(context: Context?, uris: Array<Uri>?): Array<String?>? {
        if (context == null || uris.isNullOrEmpty()) {
            return null
        }
        try {
            val paths = arrayOfNulls<String>(uris.size)
            var i = 0
            for (mUri in uris) {
                paths[i++] = RealPath.getPath(context.applicationContext, mUri)
            }
            return paths
        } catch (throwable: Throwable) {
            Timber.e(throwable)
        }

        return null
    }

    /**
     * 关闭IO流
     */
    @JvmStatic
    fun closeIO(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}