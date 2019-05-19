package cn.bsd.learn.library.core.downloader;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.bsd.learn.library.utils.IOUtils;
import cn.bsd.learn.library.utils.InputStreamUtils;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 图像通过URI从网络或文件系统或应用程序资源
 * </pre>
 */
public class BaseImageDownloader implements ImageDownloader {

    protected final Context context;
    // 连接超时15秒
    private static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 15 * 1000;
    // 读取超时15秒
    private static final int DEFAULT_HTTP_READ_TIMEOUT = 15 * 1000;
    // 连接超时次数
    private final int connectTimeout;
    // 读取超时次数
    private final int readTimeout;
    // 以字节为单位的缓冲区大小
    private static final int BUFFER_SIZE = 32 * 1024;
    // 允许扩展字符集
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    public BaseImageDownloader(Context context) {
        this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);
    }

    private BaseImageDownloader(Context context, int connectTimeout, int readTimeout) {
        this.context = context;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public InputStream getStream(String imageUri) throws IOException {
        switch (Scheme.ofUri(imageUri)) {
            case HTTP: // 跳入HTTPS执行
            case HTTPS:
                return getStreamFromNetwork(imageUri);
            case FILE:
                return getStreamFromFile(imageUri);
            case UNKNOWN:
            default:
                return getStreamFromOtherSource(imageUri);
        }
    }

    // 从网络获取流数据
    private InputStream getStreamFromNetwork(String imageUri) throws IOException {
        String encodedUrl = Uri.encode(imageUri, ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        InputStream imageStream;
        try {
            imageStream = conn.getInputStream();
        } catch (IOException e) {
            IOUtils.readAndCloseStream(conn.getErrorStream());
            throw e;
        }
        if (conn.getResponseCode() != 200) {
            IOUtils.closeSilently(imageStream);
            throw new IOException("网络请求图片失败，返回标识码： " + conn.getResponseCode());
        }

        // FileInputStream是字节流，BufferedInputStream是字节缓冲流，使用Buffered缓冲的效率很高
        return new InputStreamUtils(new BufferedInputStream(imageStream, BUFFER_SIZE), conn.getContentLength());
    }

    // BufferInputStream重写了父类FilterInputStream的mark和reset方法
    private InputStream getStreamFromFile(String imageUri) throws IOException {
        String filePath = Scheme.FILE.crop(imageUri);
        BufferedInputStream imageStream = new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE);
        return new InputStreamUtils(imageStream, (int) new File(filePath).length());
    }

    // 抛出异常
    private InputStream getStreamFromOtherSource(String imageUri) {
        throw new UnsupportedOperationException("图像URI：" + imageUri + "，不支持定义的Scheme");
    }
}

