package cn.bsd.learn.library.utils;

import android.opengl.GLES10;

import javax.microedition.khronos.opengles.GL10;

import cn.bsd.learn.library.bean.ImageSize;
import cn.bsd.learn.library.core.ViewScaleType;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 用于计算与图像大小、尺寸
 * </pre>
 */
public class ImageSizeUtils {

    // 默认Bitmap最大尺寸
    private static final int DEFAULT_MAX_BITMAP_DIMENSION = 2048;
    // 图片最大尺寸
    private static ImageSize maxBitmapSize;

    // 在开启硬件加速情况下，超大图无法正常显示（图的长宽有一个大于9000）
    // 而且程序不会crash，只是图片加载不出来，View显示为黑色
    static {
        int[] maxTextureSize = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        int maxBitmapDimension = Math.max(maxTextureSize[0], DEFAULT_MAX_BITMAP_DIMENSION);
        maxBitmapSize = new ImageSize(maxBitmapDimension, maxBitmapDimension);
    }

    private ImageSizeUtils() {
        throw new UnsupportedOperationException("ImageSizeUtils不能被构造方法初始化");
    }

    /**
     * 根据bitmap的长宽和目标缩略图的长和宽，计算出inSampleSize的大小
     */
    public static int calculateImageSampleSize(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType) {
        final int srcWidth = srcSize.getWidth();
        final int srcHeight = srcSize.getHeight();
        final int targetWidth = targetSize.getWidth();
        final int targetHeight = targetSize.getHeight();

        int scale = 1;

        switch (viewScaleType) {
            case CROP:
                // 计算最小值，a <= b ? a : b;
                scale = Math.min(srcWidth / targetWidth, srcHeight / targetHeight);
                break;
        }

        // 如果最小值小于1，则显示原图不需要降低采样
        if (scale < 1) {
            scale = 1;
        }
        // 考虑最大值超出目标最大值
        scale = considerMaxTextureSize(srcWidth, srcHeight, scale);

        return scale;
    }

    /**
     * 如果bitmap的长宽超出目标最大宽高，则不断计算降低采样值
     */
    private static int considerMaxTextureSize(int srcWidth, int srcHeight, int scale) {
        final int maxWidth = maxBitmapSize.getWidth();
        final int maxHeight = maxBitmapSize.getHeight();
        while ((srcWidth / scale) > maxWidth || (srcHeight / scale) > maxHeight) {
            scale++;
        }
        return scale;
    }

    /**
     * 根据bitmap的长宽和目标缩略图的长和宽，计算出缩放比例
     */
    public static float calculateImageScale(ImageSize srcSize, ImageSize targetSize) {
        final int srcWidth = srcSize.getWidth();
        final int srcHeight = srcSize.getHeight();
        final int targetHeight = targetSize.getHeight();
        final float heightScale = (float) srcHeight / targetHeight;
        final int destWidth = (int) (srcWidth / heightScale);

        float scale = 1;
        if (destWidth < srcWidth && targetHeight < srcHeight) {
            scale = (float) destWidth / srcWidth;
        }

        return scale;
    }
}

