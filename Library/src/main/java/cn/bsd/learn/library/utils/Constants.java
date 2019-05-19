package cn.bsd.learn.library.utils;

import android.os.Environment;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 常量工具类
 * </pre>
 */
public class Constants {

    /**
     * Log日志Tag日志前缀名
     */
    public static final String LOG_TAG = "netease.image >>> ";

    /**
     * SD卡详细路径
     */
    private static String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * 图片缓存路径
     */
    public static String IMAGES_DIR_ROOT = SDCARD_ROOT + "/Android/data/";

    /**
     * 保存图片缓存路径
     */
    public static final String IMAGE_CACHE_DIR = "/images/images_cache";
}

