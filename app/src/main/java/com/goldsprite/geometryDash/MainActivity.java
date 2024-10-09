package com.goldsprite.geometryDash;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.goldsprite.geometryDash.games.MyGame;

public class MainActivity extends AndroidApplication { 

    static MainActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        hideBlackBar(this);
        AndroidApplicationConfiguration conf = new AndroidApplicationConfiguration();
        conf.useImmersiveMode = true;
		initialize(new MyGame(), conf);
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

} 
