package cn.bsd.learn.library.core.filename;

/**
 * <pre>
 *     author  : Simon
 *     time    : 2018/12/20
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : simon@cmonbaby.com
 *     desc    : 本地缓存生成的文件名
 * </pre>
 */
public interface FileNameGenerator {

    /**
     * 为URI定义的图像生成唯一的文件名
     */
    String generate(String imageUri);
}

