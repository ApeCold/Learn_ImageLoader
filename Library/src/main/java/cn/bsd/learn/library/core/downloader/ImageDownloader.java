package cn.bsd.learn.library.core.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 提供检索，实现必须是线程安全的
 * </pre>
 */
public interface ImageDownloader {

    /**
     * 图片的URI检索
     *
     * @param imageUri 图片的URI
     */
    InputStream getStream(String imageUri) throws IOException;

    /**
     * 代表支持schemes(协议)的URI。提供方便的方法来处理schemes和URIs
     */
    enum Scheme { // [skiːm]
        HTTP("http"), HTTPS("https"), FILE("file"), UNKNOWN("");

        private String scheme;
        private String uriPrefix;

        Scheme(String scheme) {
            this.scheme = scheme;
            uriPrefix = scheme + "://";
        }

        /**
         * 定义scheme的URI
         *
         * @param uri URI的scheme检索
         * @return 带Scheme的URI
         */
        public static Scheme ofUri(String uri) {
            if (uri != null) {
                for (Scheme s : values()) {
                    if (s.belongsTo(uri)) {
                        return s;
                    }
                }
            }
            return UNKNOWN;
        }

        private boolean belongsTo(String uri) {
            return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
        }

        /**
         * 添加scheme到路径中
         */
        public String wrap(String path) {
            return uriPrefix + path;
        }

        /**
         * 从传入的URI中删除scheme部分
         * file:///storage/emulated/0/Android/data/com.netease.imageloader/images/images_cache/xxx
         * /storage/emulated/0/Android/data/com.netease.imageloader/images/images_cache/xxx
         */
        public String crop(String uri) {
            if (!belongsTo(uri)) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
            }
            return uri.substring(uriPrefix.length());
        }
    }

}

