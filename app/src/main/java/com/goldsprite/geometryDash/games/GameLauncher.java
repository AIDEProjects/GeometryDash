package com.goldsprite.geometryDash.games;
import com.goldsprite.libgdxEngine.core.scene2d.GsGame;
import com.goldsprite.geometryDash.games.screens.TexStageScreen;

public class GameLauncher extends GsGame {

    @Override
    public void create() {
        super.create();
        screen = new TexStageScreen(this);
        setScreen(screen);
    }
}
