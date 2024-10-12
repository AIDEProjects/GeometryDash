package com.goldsprite.geometryDash;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.goldsprite.geometryDash.games.GameLauncher;
import android.widget.TextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class MainActivity extends AndroidApplication { 
    static MainActivity instance;

    public static String VersionCode = "VersionCode: 241012_v3";
    LinearLayout gameLayout;

    TextView debugTxt;

    String debugString="DebugTxt";

    NoScrollScrollView debugScrollView;

    boolean showDebugTxt=true, isTouchable=true;

    SeekBar bar, playerVel, collSafeDistanceBar;

    public static float collDeathZone, //碰撞忽略区域
    playerVelVal, //玩家移速，默认30%
    collSafeDistance;//碰撞安全阈值，默认20%

    private LinearLayout rightDebugLayout;

    public static boolean showCollFullMes;

    private TextView versionCodeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        hideBlackBar(this);
        AndroidApplicationConfiguration conf = new AndroidApplicationConfiguration();
        conf.useImmersiveMode = true;
		View gameView = initializeForView(new GameLauncher(), conf);

        versionCodeTxt = findViewById(R.id.versionCodeTxt);
        versionCodeTxt.setText(VersionCode);

        gameLayout = findViewById(R.id.gameLayout);
        gameLayout.addView(gameView);

        debugTxt = findViewById(R.id.debugTxt);
        debugScrollView = findViewById(R.id.debugScrollView);
        bar = findViewById(R.id.collDeathZoneBar);
        updateCollDeathZone(bar.getProgress());
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                public void onStartTrackingTouch(SeekBar p1) {}
                public void onStopTrackingTouch(SeekBar p1) {}
                @Override
                public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                    updateCollDeathZone(progress);
                }
            });

        playerVel = findViewById(R.id.playerVel);
        playerVelVal = playerVel.getProgress() / 100f;
        playerVel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                public void onStartTrackingTouch(SeekBar p1) {}
                public void onStopTrackingTouch(SeekBar p1) {}
                @Override
                public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                    playerVelVal = progress / 100f;
                }
            });

        rightDebugLayout = findViewById(R.id.rightDebugLayout);
        collSafeDistanceBar = findViewById(R.id.collSafeDistance);
        collSafeDistanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                public void onStartTrackingTouch(SeekBar p1) {}
                public void onStopTrackingTouch(SeekBar p1) {}
                @Override
                public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                    collSafeDistance = progress/100f;
                }
            });
        collSafeDistance = collSafeDistanceBar.getProgress()/100f;
    }

    private void updateCollDeathZone(float progress) {
        collDeathZone = progress / 100f;
    }


    public void cgDebugTxt(View v) {
        showDebugTxt = !showDebugTxt;
        debugTxt.setVisibility(showDebugTxt ? TextView.VISIBLE : TextView.GONE);
        rightDebugLayout.setVisibility(showDebugTxt ? TextView.VISIBLE : TextView.GONE);
    }

    public void cgTouch(View v) {
        isTouchable = !((ToggleButton)v).isChecked();
        cgTouchM(isTouchable);
    }
    public void cgTouchM(boolean isTouchable) {
        View targetLayout = debugScrollView;
        targetLayout.setFocusable(isTouchable);
        targetLayout.setAlpha(isTouchable ? 1.0f : 0.55f); // 可见与不可触摸
    }

    public void cgCollFullMes(View v) {
        showCollFullMes = !showCollFullMes;
    }

    public static boolean stepTest;
    public void cgStepTest(View v) {
        stepTest = !stepTest;
    }


    public static void hideBlackBar(Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        activity.getWindow().setAttributes(lp);
    }

    public static void toast(final String str) {
        instance.runOnUiThread(new Runnable(){public void run() {
                    Toast.makeText(instance, str, Toast.LENGTH_SHORT).show();
                }});
    }

    public static void setDebugTxt(final String str) {
        instance.debugString = str;
        instance.runOnUiThread(new Runnable(){public void run() {
                    instance.debugTxt.setText(instance.debugString);
                    //instance.debugScrollView.fullScroll(View.FOCUS_DOWN);
                }});
    }
    public static void addDebugTxt(final String str) {
        setDebugTxt(instance.debugString + "\n" + str);
    }

} 
