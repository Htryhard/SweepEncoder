package com.xiaopao.sweepencoder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaopao.sweepencoder.model.Order;
import com.xiaopao.sweepencoder.web.AnalysisHelp;
import com.xiaopao.sweepencoder.web.WebHelp;

import org.json.JSONObject;

import java.net.URL;

public class ResultActivity extends BaseActivity {

    ImageView mResImg, mToReturn;
    private View mOrderLayout;
    private Order mOrder;
    private String mUrl = "";
    private ProgressDialog mLoading;
    private TextView mOrderNumber, mCommodityTitle, mPrice, mNum;
    private SimpleDraweeView mCommodityImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_result);
        initView();
        Intent intent = getIntent();
        //二维码中解析出的文本（链接）
        mUrl = intent.getStringExtra(CaptureActivity.EXTRA_RESULT);
        if (!mUrl.equals("")) {
            mOrder = null;
            mLoading = ProgressDialog.show(ResultActivity.this, "请稍等...", "正在读取订单......", true);
            //开启异步任务请求订单
            new CheckQRcode().execute(mUrl);
        } else {
            showError();
        }
        //二维码某一帧的图片
        mResImg.setImageBitmap((Bitmap) intent.getParcelableExtra(CaptureActivity.EXTRA_BITMAP));
        mToReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initView() {
        mResImg = (ImageView) findViewById(R.id.res_img);
        mToReturn = (ImageView) findViewById(R.id.result_return_icon);
        mOrderLayout = findViewById(R.id.result_order_layout);
        mOrderNumber = (TextView) findViewById(R.id.result_order_number);
        mCommodityTitle = (TextView) findViewById(R.id.result_commodity_title);
        mPrice = (TextView) findViewById(R.id.result_commodity_price);
        mNum = (TextView) findViewById(R.id.result_num);
        mCommodityImg = (SimpleDraweeView) findViewById(R.id.result_commodity_image);
    }

    //异步任务请二维码中的链接
    private class CheckQRcode extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String res = "";
            try {
                URL url = new URL(params[0] + "&sessionId=" + MainActivity.mUser.getSessionId());
                res = WebHelp.getJson(url);
            } catch (Exception e) {
                System.out.println("获取订单出现异常：" + e.getMessage());
            }
            return res;
        }

        @Override
        protected void onPostExecute(String res) {
            String desc = "";
            mLoading.dismiss();
            try {
                JSONObject object = new JSONObject(res);
                int code = object.getInt("code");
                String data = object.getString("data");
                desc = object.getString("desc");
                if (!desc.equals(""))
                    Toast.makeText(ResultActivity.this, desc, Toast.LENGTH_SHORT).show();
                switch (code) {
                    case 1://成功，解析订单
                        mOrder = AnalysisHelp.getOrder(data);
                        break;
                    case 2://未登录的状态
                        startActivity(new Intent(ResultActivity.this, MainActivity.class));
                        finish();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.out.println("解析订单出现异常：" + e.getMessage());
            }

            //请求成功，把数据显示到视图
            if (mOrder != null) {
                mOrderLayout.setVisibility(View.VISIBLE);
                mOrderNumber.setText(mOrder.getOrderNumber());
                mCommodityTitle.setText(mOrder.getName());
                mPrice.setText("￥:" + mOrder.getPrice());
                mNum.setText("x" + mOrder.getNum());
                if (mOrder.getImage() != null && !mOrder.getImage().equals("")) {
                    Uri uri = Uri.parse(MainActivity.GET_IMAGE + mOrder.getImage());
                    mCommodityImg.setImageURI(uri);
                }
            } else {
                showError();
            }

        }
    }

    //如果扫描的时候没有找到订单，或者扫描错误的二维码
    public void showError() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        builder.setTitle("提示！");
        builder.setMessage("没有找到订单或者二维码错误！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ResultActivity.this.finish();
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
