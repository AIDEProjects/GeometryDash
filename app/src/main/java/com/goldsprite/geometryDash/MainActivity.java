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

public class MainActivity extends AndroidApplication { 
    static MainActivity instance;

    LinearLayout gameLayout;

    TextView debugTxt;
    
    String debugString="DebugTxt";
    
    ScrollView debugScrollView;
    
    boolean showDebugTxt=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        hideBlackBar(this);
        AndroidApplicationConfiguration conf = new AndroidApplicationConfiguration();
        conf.useImmersiveMode = true;
		View gameView = initializeForView(new GameLauncher(), conf);

        gameLayout = findViewById(R.id.mainLayout);
        gameLayout.addView(gameView);

        debugTxt = findViewById(R.id.debugTxt);
        debugScrollView = findViewById(R.id.debugScrollView);
    }
    
    public void cgDebugTxt(View v){
        showDebugTxt = !showDebugTxt;
        debugTxt.setVisibility(showDebugTxt ? TextView.VISIBLE : TextView.GONE);
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
                    instance.debugScrollView.fullScroll(View.FOCUS_DOWN);
                }});
    }
    public static void addDebugTxt(final String str) {
        setDebugTxt(instance.debugString+"\n"+str);
    }

} 
