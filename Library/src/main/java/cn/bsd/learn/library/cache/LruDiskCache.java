package cn.bsd.learn.library.cache;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.bsd.learn.library.cache.core.DiskLruCache;
import cn.bsd.learn.library.core.filename.FileNameGenerator;
import cn.bsd.learn.library.utils.IOUtils;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 基于“最少最近使用”原理的磁盘缓存。适配器模式
 * </pre>
 */
public class LruDiskCache implements DiskCache {

    private static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 Kb
    private static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static final int DEFAULT_COMPRESS_QUALITY = 100;
    protected DiskLruCache cache;
    private final FileNameGenerator fileNameGenerator;
    private int bufferSize = DEFAULT_BUFFER_SIZE;
    private Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
    private DiskLruCache.Snapshot snapshot;

    /**
     * 初始化本地缓存策略
     *
     * @param cacheDir          自定义本地缓存路径
     * @param fileNameGenerator 缓存的图片文件名
     * @param cacheMaxSize      本地缓存最大Size，单位：bytes。如果为0，则表示无限空间
     * @param cacheMaxFileCount 本地缓存最大图片文件数，如果为0，则表示不限制数量
     * @throws IOException 如果缓存路径不能初始化抛出异常
     */
    public LruDiskCache(File cacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize,
                        int cacheMaxFileCount) throws IOException {
        if (cacheDir == null) {
            throw new IllegalArgumentException("cacheDir参数不能为空");
        }
        if (cacheMaxSize < 0) {
            throw new IllegalArgumentException("cacheMaxSize参数必须为正数");
        }
        if (cacheMaxFileCount < 0) {
            throw new IllegalArgumentException("cacheMaxFileCount参数必须为正数");
        }
        if (fileNameGenerator == null) {
            throw new IllegalArgumentException("fileNameGenerator参数不能为空");
        }

        if (cacheMaxSize == 0) {
            cacheMaxSize = Long.MAX_VALUE;
        }
        if (cacheMaxFileCount == 0) {
            cacheMaxFileCount = Integer.MAX_VALUE;
        }

        this.fileNameGenerator = fileNameGenerator;
        initCache(cacheDir, cacheMaxSize, cacheMaxFileCount);
    }

    private void initCache(File cacheDir, long cacheMaxSize, int cacheMaxFileCount)
            throws IOException {
        try {
            cache = DiskLruCache.open(cacheDir, 1, 1, cacheMaxSize, cacheMaxFileCount);
        } catch (IOException e) {
            e.printStackTrace();
            if (cache == null) {
                throw e; //new RuntimeException("Can't initialize disk cache", e);
            }
        }
    }

    @Override
    public File get(String imageUri) {
        try {
            snapshot = cache.get(getKey(imageUri));
            return snapshot == null ? null : snapshot.getFile(0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IOUtils.CopyListener listener) throws IOException {
        DiskLruCache.Editor editor = cache.edit(getKey(imageUri));
        if (editor == null) {
            return false;
        }

        OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), bufferSize);
        boolean copied = false;
        try {
            copied = IOUtils.copyStream(imageStream, os, listener, bufferSize);
        } finally {
            IOUtils.closeSilently(os);
            if (copied) {
                editor.commit();
            } else {
                editor.abort();
            }
        }
        return copied;
    }

    @Override
    public void save(String imageUri, Bitmap bitmap) throws IOException {
        DiskLruCache.Editor editor = cache.edit(getKey(imageUri));
        if (editor == null) {
            return;
        }

        OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), bufferSize);
        boolean savedSuccessfully;
        try {
            savedSuccessfully = bitmap.compress(compressFormat, DEFAULT_COMPRESS_QUALITY, os);
        } finally {
            IOUtils.closeSilently(os);
        }
        if (savedSuccessfully) {
            editor.commit();
        } else {
            editor.abort();
        }
    }

    @Override
    public void close() {
        try {
            cache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cache = null;
    }

    @Override
    public void clear() {
        try {
            cache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            initCache(cache.getDirectory(), cache.getMaxSize(), cache.getMaxFileCount());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getKey(String imageUri) {
        return fileNameGenerator.generate(imageUri);
    }
}

