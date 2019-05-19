package cn.bsd.learn.library.core.task;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cn.bsd.learn.library.bean.ImageDecoding;
import cn.bsd.learn.library.bean.ImageLoading;
import cn.bsd.learn.library.bean.ImageSize;
import cn.bsd.learn.library.config.ImageLoaderConfiguration;
import cn.bsd.learn.library.core.ImageLoaderEngine;
import cn.bsd.learn.library.core.LoadedFrom;
import cn.bsd.learn.library.core.ViewScaleType;
import cn.bsd.learn.library.core.decode.ImageDecoder;
import cn.bsd.learn.library.core.downloader.ImageDownloader;
import cn.bsd.learn.library.core.view.ImageAware;
import cn.bsd.learn.library.utils.Constants;
import cn.bsd.learn.library.utils.IOUtils;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 图片显示任务器，用于从网络或文件系统中加载图像，将其解码显示
 * </pre>
 */
public class DisplayTask implements Runnable, IOUtils.CopyListener {

    // 消息处理
    private Handler handler;
    // 图片加载配置
    private ImageLoaderConfiguration configuration;
    // 图片下载器
    private ImageDownloader downloader;
    // 图片解码器
    private ImageDecoder decoder;
    // 图片路径
    private String loadUrl;
    // 图片缓存key，如：http://xyz.jpg_480x800
    private String cacheKey;
    // 图片控件（属性、行为）
    private ImageAware imageAware;
    // 图片大小
    private ImageSize targetSize;
    // 图像加载来源
    private LoadedFrom loadedFrom;

    public DisplayTask(ImageLoaderEngine engine, ImageLoading imageLoading, Handler handler) {
        this.handler = handler;
        this.configuration = engine.getConfiguration();
        this.downloader = configuration.getDownloader();
        this.decoder = configuration.getDecoder();

        this.loadUrl = imageLoading.getLoadUrl();
        this.cacheKey = imageLoading.getCacheKey();
        this.imageAware = imageLoading.getImageAware();
        this.targetSize = imageLoading.getTargetSize();
    }

    @Override
    public void run() {
        Bitmap bitmap = loadBitmap();
        DisplayBitmapTask bitmapTask = new DisplayBitmapTask(bitmap, imageAware,
                configuration.getDisplayer(), loadedFrom);
        handler.post(bitmapTask);
    }

    /**
     * 开始读取Bitmap
     *
     * @return Bitmap对象
     */
    private Bitmap loadBitmap() {
        Bitmap bitmap = null;
        loadedFrom = LoadedFrom.NETWORK; // 默认从网络

        try {
            // 从本地缓存中，根据图片路径获取文件
            File imageFile = configuration.getDiskCache().get(loadUrl);
            // 如果本地存在该缓存文件，则直接开始执行Bitmap解码显示
            if (imageFile != null && imageFile.exists() && imageFile.length() > 0) {
                Log.e(Constants.LOG_TAG, "读取到本地缓存文件：" + imageFile.getAbsolutePath());
                // 从本地缓存加载图片
                loadedFrom = LoadedFrom.DISC_CACHE;
                bitmap = decodeImage(ImageDownloader.Scheme.FILE.wrap(imageFile.getAbsolutePath()));
            }

            if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
                Log.e(Constants.LOG_TAG, "检查到本地无缓存，开始从网络加载图片");
                // 从网络加载图片
                loadedFrom = LoadedFrom.NETWORK;
                // 新建临时对象，不改变引用
                String tempLoadUrl = loadUrl;
                // 设置了本地缓存，并且图片存储到本地缓存成功
                if (configuration.isCacheOnDisk() && tryCacheImageOnDisk()) {
                    // 从本地缓存获取图片文件
                    imageFile = configuration.getDiskCache().get(loadUrl);
                    if (imageFile != null) {
                        // 将本地缓存的图片文件，转成要解码为Bitmap的loadUrl
                        tempLoadUrl = ImageDownloader.Scheme.FILE.wrap(imageFile.getAbsolutePath());
                    }
                }
                // 开始解码获得Bitmap
                bitmap = decodeImage(tempLoadUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 解码图片为Bitmap
     *
     * @param imageUri 图片路径
     * @return Bitmap对象
     * @throws IOException IOException
     */
    private Bitmap decodeImage(String imageUri) throws IOException {
        // 获取ImageView的ScaleType属性
        ViewScaleType viewScaleType = imageAware.getScaleType();
        // 初始化图片解码对象
        ImageDecoding decodingInfo = new ImageDecoding(cacheKey, imageUri, targetSize, viewScaleType,
                downloader, configuration);
        // 开始解码获得Bitmap
        return decoder.decode(decodingInfo);
    }

    /**
     * 图片存储到本地缓存
     *
     * @return 是否成功
     */
    private boolean tryCacheImageOnDisk() {
        boolean loaded;
        try {
            // 是否下载成功
            loaded = downloadImage();
            if (loaded) {
                Log.e(Constants.LOG_TAG, "下载图片成功，并缓存到本地成功");
                int width = configuration.getMaxImageWidthForDiskCache();
                int height = configuration.getMaxImageHeightForDiskCache();
                // 如果配置中，设置有本地缓存宽高，则激活调整大小后，重新存储
                if (width > 0 || height > 0) {
                    Log.e(Constants.LOG_TAG, "取出缓存文件调整大小后重新存储");
                    resizeAndSaveImage(width, height);
                }
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, e.toString());
            loaded = false;
        }
        return loaded;
    }

    /**
     * 获取下载器的图片输入流，并存储该输入流到本地缓存
     *
     * @return 是否成功
     * @throws IOException IOException
     */
    private boolean downloadImage() throws IOException {
        // 赋值当前下载器的图片输入流
        InputStream is = downloader.getStream(loadUrl);
        if (is == null) {
            Log.e(Constants.LOG_TAG, "下载图片时，输入流为空");
            return false;
        } else {
            try {
                // 存储该输入流到本地缓存，返回是否成功
                return configuration.getDiskCache().save(loadUrl, is, this);
            } finally {
                // 静默关闭
                IOUtils.closeSilently(is);
            }
        }
    }

    /**
     * 获取缓存的文件，调整大小后重新保存
     *
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @throws IOException IOException
     */
    private void resizeAndSaveImage(int maxWidth, int maxHeight) throws IOException {
        // 获取缓存的文件
        File targetFile = configuration.getDiskCache().get(loadUrl);
        if (targetFile != null && targetFile.exists()) {
            // 初始化图片大小对象
            ImageSize targetImageSize = new ImageSize(maxWidth, maxHeight);
            // 初始化图片解码对象
            ImageDecoding decodingInfo = new ImageDecoding(cacheKey,
                    ImageDownloader.Scheme.FILE.wrap(targetFile.getAbsolutePath()), targetImageSize,
                    ViewScaleType.CROP, downloader, configuration);
            // 开始解码获得Bitmap
            Bitmap bitmap = decoder.decode(decodingInfo);
            if (bitmap != null) {
                configuration.getDiskCache().save(loadUrl, bitmap);
                // 最后记得回收
                bitmap.recycle();
            }
        }
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    @Override
    public boolean onBytesCopied(int current, int total) {
        // 当前线程是否处于中断状态
        return !Thread.interrupted();
    }
}
