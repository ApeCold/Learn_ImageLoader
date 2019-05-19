package cn.bsd.learn.library.core.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import cn.bsd.learn.library.bean.ImageDecoding;
import cn.bsd.learn.library.bean.ImageSize;
import cn.bsd.learn.library.core.ImageScaleType;
import cn.bsd.learn.library.utils.Constants;
import cn.bsd.learn.library.utils.IOUtils;
import cn.bsd.learn.library.utils.ImageSizeUtils;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 解码图像
 * </pre>
 */
public class BaseImageDecoder implements ImageDecoder {

    @Override
    public Bitmap decode(ImageDecoding imageDecoding) throws IOException {
        Bitmap decodedBitmap; // 解码bitmap
        ImageSize imageSize;  // 图片大小对象
        // 获取downloader下载器的输入流
        InputStream imageStream = getImageStream(imageDecoding);
        if (imageStream == null) {
            Log.e(Constants.LOG_TAG, "该图片无流：" + imageDecoding.getCacheKey());
            return null;
        }
        try {
            // 通过图片流获取图像大小对象
            imageSize = getImageSizeByStream(imageStream);
            // 获取图像大小对象后，重置输入流
            imageStream = resetStream(imageStream, imageDecoding);
            // 准备解码配置
            Options decodingOptions = prepareDecodingOptions(imageSize, imageDecoding);
            // 解码Bitmap并赋值
            decodedBitmap = BitmapFactory.decodeStream(imageStream, null, decodingOptions);
        } finally {
            // 静默关闭
            IOUtils.closeSilently(imageStream);
        }

        if (decodedBitmap == null) {
            Log.e(Constants.LOG_TAG, "该图片无法解码" + imageDecoding.getCacheKey());
        } else {
            // 解码完成后创建缩略图
            decodedBitmap = createBitmap(decodedBitmap, imageDecoding);
        }
        return decodedBitmap;
    }

    /**
     * 获取downloader下载器的输入流
     *
     * @param imageDecoding 图片解码对象
     * @return InputStream
     * @throws IOException IOException
     */
    private InputStream getImageStream(ImageDecoding imageDecoding) throws IOException {
        return imageDecoding.getDownloader().getStream(imageDecoding.getLoadUrl());
    }

    /**
     * 通过输入流获取图像大小对象
     *
     * @param imageStream 图片流
     * @return ImageSize对象
     */
    private ImageSize getImageSizeByStream(InputStream imageStream) {
        Options options = new Options();
        // 当指定inJustDecodeBounds时候，只解析图片的长度和宽度，不载入图片
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);

        return new ImageSize(options.outWidth, options.outHeight);
    }

    /**
     * 准备解码配置
     *
     * @param imageSize     图片大小对象
     * @param imageDecoding 图片解码对象
     * @return Options
     */
    private Options prepareDecodingOptions(ImageSize imageSize, ImageDecoding imageDecoding) {
        ImageSize targetSize = imageDecoding.getTargetSize();
        int scale = ImageSizeUtils.calculateImageSampleSize(imageSize, targetSize, imageDecoding.getViewScaleType());
        // 解析bitmap减少内存占用，对原图降采样，防止OOM（如：scale = 2，解析后的图片为原图1/4大小）
        Options decodingOptions = imageDecoding.getDecodingOptions();
        // 当指定inSampleSize的时候，会根据inSampleSize载入一个缩略图
        decodingOptions.inSampleSize = scale;
        return decodingOptions;
    }

    /**
     * 在该输入流中mark当前位置。后续调用reset方法重新将流定位于最后标记位置，以便后续读取能重新读取相同字节
     *
     * @param imageStream   图片流
     * @param imageDecoding 图片解码对象
     * @return InputStream 输入流
     * @throws IOException IOException
     */
    private InputStream resetStream(InputStream imageStream, ImageDecoding imageDecoding) throws IOException {
        if (imageStream.markSupported()) {
            try {
                imageStream.reset();
                return imageStream;
            } catch (IOException ignored) {
            }
        }
        // 静默关闭
        IOUtils.closeSilently(imageStream);
        return getImageStream(imageDecoding);
    }

    /**
     * 将解码后的Bitmap图像按比例缩放，创建最终Bitmap缩略图
     *
     * @param subsampledBitmap 解码后的Bitmap
     * @param imageDecoding    图片解码对象
     * @return Bitmap对象
     */
    private Bitmap createBitmap(Bitmap subsampledBitmap, ImageDecoding imageDecoding) {
        // Bitmap对象进行处理，包括：缩放、旋转、位移、倾斜等
        Matrix m = new Matrix();
        // 图像将完全按比例缩放
        ImageScaleType scaleType = imageDecoding.getImageScaleType();
        if (scaleType == ImageScaleType.EXACTLY) {
            // 获取解码后的Bitmap宽高
            ImageSize srcSize = new ImageSize(subsampledBitmap.getWidth(), subsampledBitmap.getHeight());
            // 根据bitmap的长宽和目标缩略图的长和宽，计算出缩放比例
            float scale = ImageSizeUtils.calculateImageScale(srcSize, imageDecoding.getTargetSize());
            // scale不等于1开始缩放
            if (Float.compare(scale, 1f) != 0) {
                // 设置Matrix进行缩放，sx、sy为X、Y方向上的缩放比例。
                m.setScale(scale, scale);
            }
        }

        // 创建最终Bitmap缩略图
        Bitmap finalBitmap = Bitmap.createBitmap(subsampledBitmap, 0, 0, subsampledBitmap.getWidth(), subsampledBitmap
                .getHeight(), m, true);
        // 判断缩略图A是否就是缩略图B，如果不是则回收
        if (finalBitmap != subsampledBitmap) {
            // 手动回收
            subsampledBitmap.recycle();
        }
        return finalBitmap;
    }
}

