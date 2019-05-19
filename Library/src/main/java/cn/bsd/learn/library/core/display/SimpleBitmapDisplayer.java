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
 *     desc    : 实现简单的Bitmap显示
 * </pre>
 */
public final class SimpleBitmapDisplayer implements BitmapDisplayer {

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        imageAware.setImageBitmap(bitmap);
    }

}

