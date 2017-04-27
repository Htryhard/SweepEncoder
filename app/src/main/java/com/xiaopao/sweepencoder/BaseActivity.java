package com.xiaopao.sweepencoder;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Activity的基类  所有Activity都必须继承此基类
 * Created by Huan on 2017/3/15 0015.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * 判断是否拥有权限
     * String... permissions
     * 形参String...的效果其实就和数组一样，
     * 这里的实参可以写多个String,也就是权限(下面讲到友盟分享的权限申请时就会理解)
     *
     * @param permissions
     * @return
     */
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    /**
     * 请求权限
     */
    protected void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
        Toast.makeText(this, "如果拒绝授权,会导致应用无法正常使用", Toast.LENGTH_SHORT).show();
    }

    /**
     * 请求权限的回调
     * Constants.CODE_CAMERA
     * 这是在外部封装的一个常量类，里面有许多静态的URL以及权限的CODE，可以自定义
     * 但是在调用的时候，记得这个CODE要和你自己定义的CODE一一对应
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                //请求相机的回调
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "现在您拥有了权限", Toast.LENGTH_SHORT).show();
                    startAction();
                } else {
                    Toast.makeText(this, "您拒绝授权,会导致应用无法正常使用，可以在系统设置中重新开启权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                //另一个权限的回调
                break;
            case 2:
                //另一个权限的回调
                break;
        }
    }

    //子类重写后实现具体调用相机的业务逻辑
    public void startAction() {
        //留给子类重写，这里空白就好
    }

    public void stopAction() {

    }

}
