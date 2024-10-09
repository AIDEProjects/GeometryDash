package com.goldsprite.geometryDash.games.screens;
import com.badlogic.gdx.ScreenAdapter;
import com.goldsprite.libgdxEngine.core.scene2d.LinkScreen;
import com.goldsprite.libgdxEngine.core.scene2d.GsGame;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.goldsprite.libgdxEngine.core.scene2d.actors.MyActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.goldsprite.geometryDash.MainActivity;

public class TexStageScreen extends LinkScreen {
    ShapeRenderer shapeRender = new ShapeRenderer();
    Stage stage=new Stage();

    private TextureRegion tex;

    private MyActor player;

    private int dirRight;
    private float moveVel=300, velX;


    public TexStageScreen(GsGame game) {
        super(game);

        init();
    }

    private void init() {
        Gdx.input.setInputProcessor(stage);
        stage.addListener(new MyInputListener());

        Pixmap pm = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pm.setColor(Color.GREEN);
        pm.drawRectangle(0, 0, 9, 9);
        tex = new TextureRegion(new Texture(pm));
        player = new MyActor(tex);
        player.setBounds(200, 200, 100, 100);
        stage.addActor(player);
    }

    @Override
    public void render(float delta) {
        setClearColor(1, 1, 1, 1);
        super.render(delta);

        updateLogic();

        stage.act();
        stage.draw();
    }

    private void updateLogic() {
        //更新速度
        velX = moveVel * (dirRight == 0 ?0: (dirRight > 0 ?1: -1));
        //根据速度，更新位置
        player.setPosition(player.getX() + velX * delta, player.getY());
    }


    public class MyInputListener extends InputListener {

        //原点在左下角
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            handlePlayerInput(x, y);
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            dirRight = 0;
            super.touchUp(event, x, y, pointer, button);
        }

        public void handlePlayerInput(float x, float y) {
            //下方
            if (y < Gdx.graphics.getHeight() / 2f) {
                //左或右
                dirRight = x > Gdx.graphics.getWidth() / 2f ?1: -1;
            } else {
                //上方
                System.out.println("jump");
            }
        }

    }


}
