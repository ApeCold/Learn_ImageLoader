package cn.bsd.learn.imageloader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import cn.bsd.learn.library.ImageLoaderHelper;
import cn.bsd.learn.library.config.ImageLoaderConfiguration;
import cn.bsd.learn.library.core.ImageLoader;
import cn.bsd.learn.library.utils.Constants;


public class MainActivity extends AppCompatActivity {

    String s1 = "http://g.hiphotos.baidu.com/image/pic/item/0823dd54564e92589f2fe1019882d158cdbf4ec1.jpg";
    String s2 = "http://f.hiphotos.baidu.com/image/pic/item/4e4a20a4462309f735600bfe760e0cf3d6cad6cb.jpg";
    String s3 = "http://e.hiphotos.baidu.com/image/pic/item/72f082025aafa40f6f6d3209af64034f79f019be.jpg";
    String s4 = "http://e.hiphotos.baidu.com/image/pic/item/8cb1cb134954092359d94e479758d109b3de4952.jpg";
    String s5 = "http://g.hiphotos.baidu.com/image/pic/item/10dfa9ec8a1363270c254f53948fa0ec09fac782.jpg";
    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private ImageView iv4;
    private ImageView iv5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        iv3 = findViewById(R.id.iv3);
        iv4 = findViewById(R.id.iv4);
        iv5 = findViewById(R.id.iv5);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }
    }

    private void loader() {
        ImageLoaderConfiguration.init(this, Constants.IMAGES_DIR_ROOT
                + getPackageName() + Constants.IMAGE_CACHE_DIR);

        ImageLoaderHelper.with().from(s1).into(iv1).display();
        ImageLoaderHelper.with().from(s2).into(iv2).display();
        ImageLoaderHelper.with().from(s3).into(iv3).display();
        ImageLoaderHelper.with().from(s4).into(iv4).display();
        ImageLoaderHelper.with().from(s5).into(iv5).display();
    }

    @Override
    protected void onDestroy() {
        ImageLoader.getInstance().destroy();
        super.onDestroy();
    }

    public void loader(View view) {
        loader();
    }

    public void clear(View view) {
        ImageLoader.getInstance().clearDiskCache();
    }
}
