package cn.bsd.learn.library.core.task;

import android.graphics.Bitmap;

import cn.bsd.learn.library.core.LoadedFrom;
import cn.bsd.learn.library.core.display.BitmapDisplayer;
import cn.bsd.learn.library.core.view.ImageAware;


/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 在ImageView中显示图片，必须在UI线程上调用
 * </pre>
 */
final class DisplayBitmapTask implements Runnable {

    private final Bitmap bitmap;
    private final ImageAware imageAware;
    private final BitmapDisplayer displayer;
    private final LoadedFrom loadedFrom;

    DisplayBitmapTask(Bitmap bitmap, ImageAware imageAware, BitmapDisplayer displayer, LoadedFrom loadedFrom) {
        this.bitmap = bitmap;
        this.imageAware = imageAware;
        this.displayer = displayer;
        this.loadedFrom = loadedFrom;
    }

    @Override
    public void run() {
        displayer.display(bitmap, imageAware, loadedFrom);
    }
}

