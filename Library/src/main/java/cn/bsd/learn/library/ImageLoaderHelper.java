package cn.bsd.learn.library;

import android.widget.ImageView;

import cn.bsd.learn.library.core.ImageLoader;


/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 图片加载辅助类
 * </pre>
 */
public class ImageLoaderHelper {

    private ImageLoader imageLoader;
    private ImageView imageView;
    private String loadUrl;

    private ImageLoaderHelper(Builder builder) {
        imageLoader = ImageLoader.getInstance();
        this.imageView = builder.imageView;
        this.loadUrl = builder.loadUrl;
        display();
    }

    private void display() {
        if (imageView == null) return;
        imageLoader.displayImage(loadUrl, imageView);
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {

        private ImageView imageView;
        private String loadUrl;

        public Builder from(String loadUrl) {
            this.loadUrl = loadUrl;
            return this;
        }

        public Builder into(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public void display() {
            new ImageLoaderHelper(this);
        }
    }
}
