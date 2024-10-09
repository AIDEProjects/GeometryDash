package com.goldsprite.geometryDash.games;
import com.goldsprite.libgdxEngine.core.scene2d.GsGame;

public class GameLauncher extends GsGame {

    @Override
    public void create() {
        super.create();
        new TexStageScreen(this);
        setScreen(screen);
    }
}
