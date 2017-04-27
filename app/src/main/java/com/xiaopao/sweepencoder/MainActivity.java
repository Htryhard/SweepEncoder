package com.xiaopao.sweepencoder;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaopao.sweepencoder.model.User;
import com.xiaopao.sweepencoder.tools.SharedPreferencesTool;
import com.xiaopao.sweepencoder.web.AnalysisHelp;
import com.xiaopao.sweepencoder.web.WebHelp;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends BaseActivity {

    private Button mLogin;
    public static User mUser = null;
    private EditText mMobile, mPassword;
    public static String API_URL = "http://192.168.1.123:8080/distribution/api/";
    public static String GET_IMAGE = "http://192.168.1.123:8080/distribution";
    private String mobileStr = "";
    private String passwordStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //自动补上账号密码
        mobileStr = SharedPreferencesTool.getString(MainActivity.this, "mobile");
        passwordStr = SharedPreferencesTool.getString(MainActivity.this, "password");
        if (!mobileStr.equals("") && !passwordStr.equals("")) {
            mMobile.setText(mobileStr);
            mPassword.setText(passwordStr);
        }
        //登录按钮
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:开启异步任务验证用户
                //简单验证账号的格式
                mLogin.setText("登录中...");
                mobileStr = mMobile.getText().toString();
                passwordStr = mPassword.getText().toString();
                if (mobileStr.equals("") || passwordStr.equals("")) {
                    Toast.makeText(MainActivity.this, "手机号码或者密码不能为空！", Toast.LENGTH_SHORT).show();
                } else if (!isMobile(mobileStr)) {
                    Toast.makeText(MainActivity.this, "手机号码格式不正确！", Toast.LENGTH_SHORT).show();
                } else {
                    new LoginTask().execute(mobileStr, passwordStr);
                }
            }
        });
    }

    //初始化视图
    public void initView() {
        mLogin = (Button) findViewById(R.id.login_post);
        mMobile = (EditText) findViewById(R.id.login_edit_phone);
        mPassword = (EditText) findViewById(R.id.login_edit_password);
    }

    //检查是否授权摄像头、读写之类的权限
    public void initPermission() {
        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        //判断是否有权限
        if (hasPermission(permission)) {
            //有权限，则写业务逻辑
            startAction();
        } else {
            //没权限，进行权限请求
            requestPermission(0, permission);
            stopAction();
        }
    }

    //验证手机号
    public boolean isMobile(String mobiles) {
        int len = mobiles.length();
        boolean flag = true;
        if (len != 11) {
            flag = false;
        }
        if (!mobiles.substring(0, 1).equals("1") || mobiles.substring(1, 2).equals("2")) {
            flag = false;
        }
        return flag;
    }

    public void startAction() {
        //开始扫一扫
        startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), 0);
    }

    //主要用于二维码的回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Toast.makeText(MainActivity.this, "扫描的内容：" + data.getStringExtra(CaptureActivity.EXTRA_RESULT), Toast.LENGTH_SHORT).show();
            Log.e("==二维码扫描==", data.getStringExtra(CaptureActivity.EXTRA_RESULT));
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("resImg", data.getParcelableExtra(CaptureActivity.EXTRA_BITMAP));
            startActivity(intent);
            //解析出的内容
//            mResTxt.setText("扫码的结果：" + data.getStringExtra(CaptureActivity.EXTRA_RESULT));
            //所获取的一帧图像
//            mResImg.setImageBitmap((Bitmap) data.getParcelableExtra(CaptureActivity.EXTRA_BITMAP));
        } else {
//            mResTxt.setText("");
//            mResImg.setImageDrawable(null);
        }
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String res = "";
            try {
//                user = WebHelp.sendPost()
                URL url = new URL(API_URL + "user/login?mobile=" + params[0] + "&password=" + params[1]);
                res = WebHelp.getJson(url);
            } catch (Exception e) {
                System.out.println("登录请求出现异常：" + e.getMessage());
            }
            return res;
        }

        @Override
        protected void onPostExecute(String res) {
            Log.e("==++res++==", res);
            mLogin.setText("登录");
            String desc = "";
            User user = null;
            try {
                JSONObject object = new JSONObject(res);
                int code = object.getInt("code");
                String data = object.getString("data");
                desc = object.getString("desc");
                switch (code) {
                    case 1:
                        user = AnalysisHelp.getUser(data);
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!desc.equals(""))
                Toast.makeText(MainActivity.this, desc, Toast.LENGTH_SHORT).show();
            if (user != null) {
                //登录成功，标记用户，记住账号密码
                mUser = user;
                SharedPreferencesTool.putString(MainActivity.this, "mobile", mobileStr);
                SharedPreferencesTool.putString(MainActivity.this, "password", passwordStr);
                //验证权限，并且开启扫描
                initPermission();
            }
        }
    }
}
