package cn.bsd.learn.library.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 应用存储路径 (SD卡：/Android/data/[package_name]/images/images_cache)
 * </pre>
 */
public class StorageUtils {

    private StorageUtils() {
        throw new UnsupportedOperationException("StorageUtils不能被构造方法初始化");
    }

    /**
     * 获取目录名
     *
     * @param filePath 完整路径名
     */
    private static String getFolderName(String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    /**
     * 创建指定的多级目录
     *
     * @param filePath 完整路径名
     */
    public static void makeFolders(String filePath) {
        String folderName = getFolderName(filePath);
        // mkdirs()可以建立多级文件夹，mkdir()只会建立一级的文件夹
        boolean bool = new File(folderName).mkdirs();
        Log.e(Constants.LOG_TAG, "创建本地多级缓存目录：" + bool + " >>> " + folderName);
    }

    /**
     * 该目录地址是否存在，并且是一个目录
     *
     * @param directoryPath 目录地址
     */
    public static boolean isFolderExist(String directoryPath) {
        if (TextUtils.isEmpty(directoryPath)) {
            return false;
        }

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }
}

