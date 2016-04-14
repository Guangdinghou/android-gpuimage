package com.guang.sun.game.camera;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.game2048.R;
import com.guang.sun.game.camera.preview.CameraSurfaceView;
import com.guang.sun.game.camera.util.DisplayUtil;
import com.squareup.picasso.Picasso;

import java.io.File;


public class CameraActivity extends Activity implements CameraInterface.CamOpenOverCallback {
    private static final String TAG = "yanzi";
    CameraSurfaceView surfaceView = null;
    ImageButton shutterBtn;
    float previewRate = -1f;
    ImageView iv;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Log.i("message", "--");
            finish();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.anim_fade_in, android.R.anim.fade_out);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_camera);
        iv = (ImageView) findViewById(R.id.iv);
        iv.setOnClickListener(new BtnListeners());
        System.out.print("时间" + System.currentTimeMillis());

        Picasso.with(this).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Application/a.jpg")).into(iv);
        Thread openThread = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                CameraInterface.getInstance().doOpenCamera(CameraActivity.this);
            }
        };
        openThread.start();
        initUI();
        initViewParams();
        regBroadCast();
        shutterBtn.setOnClickListener(new BtnListeners());
    }

    private ReceiveBroadCast receiveBroadCast;

    public void regBroadCast() {
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("2048");    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(receiveBroadCast, filter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onCreate(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Picasso.with(this).invalidate(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Application/a.jpg"));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    private void initUI() {
        surfaceView = (CameraSurfaceView) findViewById(R.id.camera_surfaceview);
        shutterBtn = (ImageButton) findViewById(R.id.btn_shutter);
    }

    private void initViewParams() {
        LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
        surfaceView.setLayoutParams(params);

        //手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
        LayoutParams p2 = shutterBtn.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);
        ;
        shutterBtn.setLayoutParams(p2);

    }

    public class ReceiveBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //得到广播中得到的数据，并显示出来
            Toast.makeText(CameraActivity.this, "get", Toast.LENGTH_SHORT).show();
            Picasso.with(CameraActivity.this).load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Application/a.jpg")).into(iv);
        }

    }

    @Override
    public void cameraHasOpened() {
        // TODO Auto-generated method stub
        SurfaceHolder holder = surfaceView.getSurfaceHolder();
        CameraInterface.getInstance().doStartPreview(holder, previewRate);
    }


    private class BtnListeners implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_shutter:
                    CameraInterface.getInstance().doTakePicture();
                    break;
                case R.id.iv:
                    CameraInterface.getInstance().doTakePicture();

                    handler.sendEmptyMessageDelayed(0, 1000);
                    iv.setClickable(false);
                    break;
                default:
                    break;
            }
        }

    }

}
