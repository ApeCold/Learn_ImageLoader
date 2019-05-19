package cn.bsd.learn.library.core;

import android.widget.ImageView;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 说明：https://www.cnblogs.com/pandapan/p/4614837.html
 * </pre>
 */
public enum ViewScaleType {

    CENTER_INSIDE,
    CROP;

    //  [skeɪl]
    public static ViewScaleType fromImageView(ImageView imageView) {
        switch (imageView.getScaleType()) {
            case FIT_CENTER:
            case FIT_XY:
            case FIT_START:
            case FIT_END:
            case CENTER_INSIDE:
                return CENTER_INSIDE;
            case MATRIX:
            case CENTER:
            case CENTER_CROP:
            default:
                return CROP;
        }
    }

}

