package cn.bsd.learn.library.core.view;

import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;

import cn.bsd.learn.library.core.ViewScaleType;
import cn.bsd.learn.library.utils.Constants;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 实现图像处理和显示的属性和行为
 * </pre>
 */
public class ImageViewAware implements ImageAware {

    private ImageView imageView;

    public ImageViewAware(ImageView imageView) {
        if (imageView == null) throw new IllegalArgumentException("ImageView不能为空");
        this.imageView = imageView;
    }

    @Override
    public int getWidth() {
        if (imageView != null) {
            final ViewGroup.LayoutParams params = imageView.getLayoutParams();
            int width = 0;
            if (params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
                width = imageView.getWidth();
            }
            if (width <= 0 && params != null) {
                width = params.width;
            }
            if (width <= 0 && imageView != null) {
                width = getImageViewFieldValue(imageView, "mMaxWidth");
            }
            return width;
        }
        return 0;
    }

    @Override
    public int getHeight() {
        if (imageView != null) {
            int height = 0;
            final ViewGroup.LayoutParams params = imageView.getLayoutParams();
            if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = imageView.getHeight();
            }
            if (height <= 0 && params != null) {
                height = params.height;
            }
            if (height <= 0 && imageView != null) {
                height = getImageViewFieldValue(imageView, "mMaxHeight");
            }
            return height;
        }
        return 0;
    }

    @Override
    public ViewScaleType getScaleType() {
        if (imageView != null) {
            return ViewScaleType.fromImageView(imageView);
        }
        return ViewScaleType.CROP;
    }

    @Override
    public int getId() {
        return imageView == null ? super.hashCode() : imageView.hashCode();
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        // 主线程显示
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        } else {
            Log.e(Constants.LOG_TAG, "不能设置bitmap到ImageView中，必须在UI线程中才能调用");
        }
    }

    /**
     * 通过反射获取ImageView中的宽高属性
     *
     * @param object    ImageView对象
     * @param fieldName 需要反射的属性名
     * @return 值
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}

