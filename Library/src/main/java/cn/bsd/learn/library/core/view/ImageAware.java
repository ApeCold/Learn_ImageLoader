package cn.bsd.learn.library.core.view;

import android.graphics.Bitmap;

import cn.bsd.learn.library.core.ViewScaleType;


/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 提供图像处理和显示的属性和行为
 * </pre>
 */
public interface ImageAware {

    int getWidth();

    int getHeight();

    ViewScaleType getScaleType();

    int getId();

    void setImageBitmap(Bitmap bitmap);

}

