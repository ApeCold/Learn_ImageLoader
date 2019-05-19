package cn.bsd.learn.library.config;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import cn.bsd.learn.library.bean.ImageSize;
import cn.bsd.learn.library.cache.DiskCache;
import cn.bsd.learn.library.cache.LruDiskCache;
import cn.bsd.learn.library.core.ImageLoader;
import cn.bsd.learn.library.core.ImageScaleType;
import cn.bsd.learn.library.core.decode.BaseImageDecoder;
import cn.bsd.learn.library.core.decode.ImageDecoder;
import cn.bsd.learn.library.core.display.BitmapDisplayer;
import cn.bsd.learn.library.core.display.SimpleBitmapDisplayer;
import cn.bsd.learn.library.core.downloader.BaseImageDownloader;
import cn.bsd.learn.library.core.downloader.ImageDownloader;
import cn.bsd.learn.library.core.filename.FileNameGenerator;
import cn.bsd.learn.library.core.filename.Md5FileNameGenerator;
import cn.bsd.learn.library.utils.Constants;
import cn.bsd.learn.library.utils.StorageUtils;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 图片加载配置
 * </pre>
 */
public class ImageLoaderConfiguration {

    // Resources资源
    private Resources resources;
    // 本地缓存图片最大宽度（不设置则为原图长宽）
    private int maxImageWidthForDiskCache;
    // 本地缓存图片最大高度（不设置则为原图长宽）
    private int maxImageHeightForDiskCache;
    // 本地缓存
    private DiskCache diskCache;
    // 图片下载器
    private ImageDownloader downloader;
    // 图片解码器
    private ImageDecoder decoder;
    // 下载的图片是否缓存在SD卡中
    private boolean cacheOnDisk;
    // 图片显示的编码方式（完全按比例缩小）
    private ImageScaleType imageScaleType;
    // 图片解码设置
    private Options decodingOptions;
    // 显示图片器
    private BitmapDisplayer displayer;

    private ImageLoaderConfiguration(Builder builder) {
        resources = builder.context.getResources();
        diskCache = builder.diskCache;
        downloader = builder.downloader;
        decoder = builder.decoder;
        displayer = builder.displayer;

        // 缓存在SD卡中
        cacheOnDisk = true;
        // 本地缓存图片最大宽度
        maxImageWidthForDiskCache = 480;
        // 本地缓存图片最大高度
        maxImageHeightForDiskCache = 800;
        // 图像将完全按比例缩小的目标大小
        imageScaleType = ImageScaleType.EXACTLY;
        decodingOptions = new Options();
        // 使用RGB_565会比使用ARGB_8888少消耗2倍的内存
        decodingOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    public static void init(@NonNull Context context, String cacheDisk) {
        ImageLoader.getInstance().init(simpleConfig(context, cacheDisk));
    }

    private static ImageLoaderConfiguration simpleConfig(Context context, String cacheDisk) {
        File cacheDir; // 缓存文件的目录
        if (TextUtils.isEmpty(cacheDisk)) {
            cacheDisk = Constants.IMAGES_DIR_ROOT + context.getPackageName() + Constants.IMAGE_CACHE_DIR;
        }
        if (!StorageUtils.isFolderExist(cacheDisk)) {
            StorageUtils.makeFolders(cacheDisk);
        }
        cacheDir = new File(cacheDisk);
        return new Builder(context)
                // 本地缓存配置（自定义缓存路径，MD5图片名，缓存大小，文件数量）
                .diskCache(createDiskCache(cacheDir, new Md5FileNameGenerator()))
                // 连接超时 (默认15s), 读取超时 (默认15s)
                .downloader(new BaseImageDownloader(context))
                // 解码器
                .decoder(new BaseImageDecoder())
                // Bitmap显示器
                .displayer(new SimpleBitmapDisplayer())
                .build();
    }

    /**
     * 创建本地缓存（自定义缓存路径，MD5图片名，本地缓存大小，本地缓存文件数量）
     *
     * @param cacheDir                   缓存路径
     * @param diskCacheFileNameGenerator 图片文件名生成器
     */
    private static DiskCache createDiskCache(File cacheDir, FileNameGenerator diskCacheFileNameGenerator) {
        // 只有配置了本地缓存大小或者本地缓存文件数才能激活Lru算法
        try {
            return new LruDiskCache(cacheDir, diskCacheFileNameGenerator,
                    100 * 1024 * 1024, 200);
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "初始化配置，创建本地缓存出现异常" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public int getMaxImageWidthForDiskCache() {
        return maxImageWidthForDiskCache;
    }

    public int getMaxImageHeightForDiskCache() {
        return maxImageHeightForDiskCache;
    }

    public DiskCache getDiskCache() {
        return diskCache;
    }

    public ImageDownloader getDownloader() {
        return downloader;
    }

    public ImageDecoder getDecoder() {
        return decoder;
    }

    public boolean isCacheOnDisk() {
        return cacheOnDisk;
    }

    public ImageScaleType getImageScaleType() {
        return imageScaleType;
    }

    public Options getDecodingOptions() {
        return decodingOptions;
    }

    public BitmapDisplayer getDisplayer() {
        return displayer;
    }

    /**
     * 获取图片对象的尺寸，宽高为：本地缓存图片最大宽高
     */
    public ImageSize getMaxImageSize() {
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        int width = maxImageWidthForDiskCache;
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }
        int height = maxImageHeightForDiskCache;
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        return new ImageSize(width, height);
    }

    public static class Builder {

        // 上下文
        private Context context;
        // 本地缓存
        private DiskCache diskCache;
        // 图片下载器
        private ImageDownloader downloader;
        // 图片解码器
        private ImageDecoder decoder;
        // 显示图片器
        private BitmapDisplayer displayer;

        Builder(@NonNull Context context) {
            this.context = context;
        }

        /**
         * 设置本地缓存
         */
        Builder diskCache(DiskCache diskCache) {
            this.diskCache = diskCache;
            return this;
        }

        /**
         * 设置图像下载器
         */
        Builder downloader(ImageDownloader imageDownloader) {
            this.downloader = imageDownloader;
            return this;
        }

        /**
         * 设置图像解码器
         */
        Builder decoder(ImageDecoder decoder) {
            this.decoder = decoder;
            return this;
        }

        /**
         * 设置显示图片器
         */
        Builder displayer(BitmapDisplayer displayer) {
            this.displayer = displayer;
            return this;
        }

        /**
         * 构造配置
         */
        ImageLoaderConfiguration build() {
            return new ImageLoaderConfiguration(this);
        }
    }
}
