package cn.bsd.learn.library.core.display;

import android.graphics.Bitmap;

import cn.bsd.learn.library.core.LoadedFrom;
import cn.bsd.learn.library.core.view.ImageAware;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : Bitmap显示接口
 * </pre>
 */
public interface BitmapDisplayer {

    void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom);

}

