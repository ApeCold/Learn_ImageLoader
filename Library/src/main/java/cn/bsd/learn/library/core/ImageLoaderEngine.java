package cn.bsd.learn.library.core;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cn.bsd.learn.library.config.ImageLoaderConfiguration;
import cn.bsd.learn.library.core.task.DisplayTask;
import cn.bsd.learn.library.utils.Constants;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 图片加载引擎，调度器
 * </pre>
 */
public class ImageLoaderEngine {

    // 图片加载配置
    private ImageLoaderConfiguration configuration;
    // 普通任务
    private Executor taskExecutor;
    // 本地缓存任务
    private Executor taskExecutorForCachedImages;
    // 任务分配器
    private Executor taskDistributor;
    // 线程池大小
    private static final int DEFAULT_THREAD_POOL_SIZE = 3;
    // 线程优先级
    private static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;

    ImageLoaderEngine(ImageLoaderConfiguration configuration) {
        this.configuration = configuration;

        taskExecutor = createExecutor();
        taskExecutorForCachedImages = createExecutor();
        taskDistributor = createTaskDistributor();
    }

    /**
     * 创建任务执行器
     */
    private Executor createExecutor() {
        // 阻塞的线程安全的队列，底层采用链表实现
        //  [kjuː]
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        return new ThreadPoolExecutor(DEFAULT_THREAD_POOL_SIZE, DEFAULT_THREAD_POOL_SIZE,
                0L, TimeUnit.MILLISECONDS, taskQueue,
                createThreadFactory(DEFAULT_THREAD_PRIORITY, "images-pool-"));
    }

    /**
     * 创建任务分配器
     */
    private Executor createTaskDistributor() {
        // 创建一个可缓存线程池，线程级别：5，线程名前缀：images-pool-d-
        return Executors.newCachedThreadPool(createThreadFactory(Thread.NORM_PRIORITY, "images-pool-d-"));
    }

    public ImageLoaderConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * 提交任务，开始执行图片显示
     *
     * @param task 图片显示任务器，用于从网络或文件系统中加载图像，将其解码显示
     */
    void submit(final DisplayTask task) {
        taskDistributor.execute(new Runnable() {
            @Override
            public void run() {
                // 从本地缓存中，根据图片路径获取文件
                File image = configuration.getDiskCache().get(task.getLoadUrl());
                boolean isImageCachedOnDisk = image != null && image.exists();
                // 如果本地存在，说明已经缓存，则调度缓存任务执行
                if (isImageCachedOnDisk) {
                    Log.e(Constants.LOG_TAG, "调度缓存任务");
                    taskExecutorForCachedImages.execute(task);
                } else {
                    // 网络加载任务
                    Log.e(Constants.LOG_TAG, "调度网络加载任务");
                    taskExecutor.execute(task);
                }
            }
        });
    }

    /**
     * 创建线程工厂
     *
     * @param threadPriority   线程优先级
     * @param threadNamePrefix 线程名前缀
     */
    private ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
    }

    /**
     * 线程工厂
     */
    private class DefaultThreadFactory implements ThreadFactory {
        // 被多个线程并发访问时可以得到正确的结果，也就是实现线程安全
        private AtomicInteger poolNumber = new AtomicInteger(1); // 线程池数量
        private ThreadGroup group; // 线程组
        // 使用非阻塞算法来实现并发控制
        private AtomicInteger threadNumber = new AtomicInteger(1); // 线程数量
        private String namePrefix; // 名称前缀
        private int threadPriority; // 线程优先级

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            // 获取当前线程组
            group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Log.e(Constants.LOG_TAG, "线程工厂创建自定义线程");
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            // 检查线程是否为守护程序线程
            if (t.isDaemon()) t.setDaemon(false);
            t.setPriority(threadPriority);
            return t;
        }
    }
}
