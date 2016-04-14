package com.guang.sun.game;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.game2048.R;
import com.guang.sun.game.base.MyApplication;
import com.guang.sun.game.camera.CameraActivity;
import com.guang.sun.game.camera.util.DisplayUtil;
import com.guang.sun.game.floatwindowdemo.FloatWindowService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity implements GameView.ITakePhoto {

    private TextView scoreTextView, bestScoreTextView;
    private Button buttoNewGame;
    private GameView gameView = null;
    private int width, height;
    private Score score;

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        app = new MyApplication();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        buttoNewGame = (Button) findViewById(R.id.btnNewGame);
        scoreTextView = (TextView) findViewById(R.id.score);
        bestScoreTextView = (TextView) findViewById(R.id.bestScore);

        gameView = (GameView) findViewById(R.id.gameView);

        score = new Score();
        gameView.setScore(score);
        gameView.setMyOnClick(this);
        int w = this.getResources().getDisplayMetrics().widthPixels;
        w = w * 19 / 20;
        ViewGroup.LayoutParams lp;
        lp = gameView.getLayoutParams();
        lp.width = w;
        lp.height = w;
        width = DisplayUtil.getScreenMetrics(this).x;
        height = DisplayUtil.getScreenMetrics(this).y;
        gameView.setLayoutParams(lp);


        app.setStatusBarHeight(getStatusBarHeight());
        System.out.println("first--->" + getStatusBarHeight());
        Button startFloatWindow = (Button) findViewById(R.id.start_float_window);
        startFloatWindow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
                startService(intent);
                finish();
            }
        });
        buttoNewGame.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startNewGame();
            }
        });
//        MyOpenCmera();
        gameView.setLines(4);


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void startNewGame() {
        score.clearScore();
        showScore();
        showBestScore();
        gameView.startGame();
    }

    private void showBestScore() {
        bestScoreTextView.setText(String.valueOf(score.getBestScore()));
    }

    private void showScore() {
        scoreTextView.setText(String.valueOf(score.getScore()));
    }

    static Long t1;
    static Long t2;
    MyApplication app;

    public void getScreenCut() {
        t1 = System.currentTimeMillis();
        View home = gameView.getRootView();
        home.setDrawingCacheEnabled(true);
        Bitmap bm = home.getDrawingCache();

        System.out.println("second--->" + app.getStatusBarHeight());

        // 获取屏幕长和高

        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(bm, 0, app.getStatusBarHeight(), width, height
                - app.getStatusBarHeight());
        home.destroyDrawingCache();


        savePic(b, Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Application/a.jpg");
        Intent intent = new Intent();  //Itent就是我们要发送的内容
        intent.putExtra("data", "t");
        intent.setAction("2048");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
        sendBroadcast(intent);
    }

    @Override
    public void takePhoto() {
        //点击后拍照
//        Intent i = new Intent(MainActivity.this, CameraActivity.class);
//        startActivity(i);
//        Thread t = new Thread(
//                new Runnable() {
//            @Override
//            public void run() {
//                getScreenCut();
//            }
//        });
//        t.start();


    }

    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                b.recycle();
                t2 = System.currentTimeMillis();
                System.out.println("消耗时间：" + (t2 - t1) + "===" + t1 + "===" + t2);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class Score {
        private static final String SP_KEY_BEST_SCORE = "bestScore";
        private int score = 0;

        public void clearScore() {
            score = 0;
        }

        public int getScore() {
            return score;
        }

        public void addScore(int s) {
            score += s;
            showScore();

            saveBestScore(Math.max(score, getBestScore()));
            showBestScore();
        }

        public void saveBestScore(int s) {
            Editor e = getPreferences(MODE_PRIVATE).edit();
            e.putInt(SP_KEY_BEST_SCORE, s);
            e.commit();
        }

        public int getBestScore() {
            return getPreferences(MODE_PRIVATE).getInt(SP_KEY_BEST_SCORE, 0);
        }


    }


}
