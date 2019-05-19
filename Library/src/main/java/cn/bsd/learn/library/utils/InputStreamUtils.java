package cn.bsd.learn.library.utils;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : InputStream处理器
 * </pre>
 */
public class InputStreamUtils extends InputStream {

    private final InputStream stream;
    private final int length;

    public InputStreamUtils(InputStream stream, int length) {
        this.stream = stream;
        this.length = length;
    }

    @Override
    public int available() {
        return length;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void mark(int readLimit) {
        stream.mark(readLimit);
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public int read(@NonNull byte[] buffer) throws IOException {
        return stream.read(buffer);
    }

    @Override
    public int read(@NonNull byte[] buffer, int byteOffset, int byteCount) throws IOException {
        return stream.read(buffer, byteOffset, byteCount);
    }

    @Override
    public void reset() throws IOException {
        stream.reset();
    }

    @Override
    public long skip(long byteCount) throws IOException {
        return stream.skip(byteCount);
    }

    @Override
    public boolean markSupported() {
        return stream.markSupported();
    }

}

