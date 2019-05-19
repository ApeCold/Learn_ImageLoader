package cn.bsd.learn.library.bean;

import android.graphics.BitmapFactory.Options;

import cn.bsd.learn.library.config.ImageLoaderConfiguration;
import cn.bsd.learn.library.core.ImageScaleType;
import cn.bsd.learn.library.core.ViewScaleType;
import cn.bsd.learn.library.core.downloader.ImageDownloader;


/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 图片解码对象
 * </pre>
 */
public class ImageDecoding {

    // 图片路径
    private String loadUrl;
    // 图片缓存key，如：http://xyz.jpg_480x800
    private String cacheKey;
    // 图片大小
    private ImageSize targetSize;
    // 图片显示的编码方式（完全按比例缩小）
    private ImageScaleType imageScaleType;
    // ImageView的ScaleType属性
    private ViewScaleType viewScaleType;
    // 图片下载器
    private ImageDownloader downloader;
    // 图片解码设置
    private Options decodingOptions;

    public ImageDecoding(String cacheKey, String loadUrl, ImageSize targetSize, ViewScaleType viewScaleType,
                         ImageDownloader downloader, ImageLoaderConfiguration configuration) {
        this.loadUrl = loadUrl;
        this.cacheKey = cacheKey;
        this.targetSize = targetSize;
        this.imageScaleType = configuration.getImageScaleType();
        this.viewScaleType = viewScaleType;
        this.downloader = downloader;
        decodingOptions = configuration.getDecodingOptions();
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public ImageSize getTargetSize() {
        return targetSize;
    }

    public ImageScaleType getImageScaleType() {
        return imageScaleType;
    }

    public ViewScaleType getViewScaleType() {
        return viewScaleType;
    }

    public ImageDownloader getDownloader() {
        return downloader;
    }

    public Options getDecodingOptions() {
        return decodingOptions;
    }
}

