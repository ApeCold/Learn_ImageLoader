package cn.bsd.learn.library.core.decode;

import android.graphics.Bitmap;

import java.io.IOException;

import cn.bsd.learn.library.bean.ImageDecoding;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 图片解码接口
 * </pre>
 */
public interface ImageDecoder {

    Bitmap decode(ImageDecoding imageDecoding) throws IOException;

}
