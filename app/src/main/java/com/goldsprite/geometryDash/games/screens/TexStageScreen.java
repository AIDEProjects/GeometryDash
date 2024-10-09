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

    private MyActor ground;


    public TexStageScreen(GsGame game) {
        super(game);

        init();
    }

    private void init() {
        Gdx.input.setInputProcessor(stage);
        stage.addListener(new MyInputListener());

        createPlayer();

        createGround();
    }

    private void createPlayer() {
        Pixmap pm = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pm.setColor(Color.GREEN);
        pm.drawRectangle(0, 0, 9, 9);
        tex = new TextureRegion(new Texture(pm));
        player = new MyActor(tex);
        player.setBounds(200, 400, player.getWidth()*10, player.getHeight()*10);
        stage.addActor(player);
    }

    private void createGround() {

        Pixmap pm = new Pixmap(140, 50, Pixmap.Format.RGBA8888);
        pm.setColor(Color.BLUE);
        /*
        pm.drawLine(0, 7, 13, 7);
        pm.drawLine(13, 7, 13, 0);
        pm.drawLine(13, 0, 36, 0);
        pm.drawLine(36, 0, 36, 7);
        pm.drawLine(36, 7, 49, 7);
        pm.drawLine(49, 7, 49, 19);
        pm.drawLine(49, 19, 0, 19);
        pm.drawLine(0, 19, 0, 7);
        */
        float[] lines={
            
            0, 0.1f, 
            0, 0.45f, 
            0.26f, 0.45f, 
            0.45f, 1f, 
            0.72f, 1f, 
            0.72f, 0.45f, 
            0.99f, 0.45f, 
            0.99f, 0.1f, 
            0, 0.1f
        };
        for (int i=0,i2=2;i < lines.length;i += 2, i2 = (i + 2) % lines.length) {
            pm.drawLine(
                (int)(lines[i] * pm.getWidth()), 
                (int)((1-lines[i + 1]) * pm.getHeight()), 
                (int)(lines[i2] * pm.getWidth()), 
                (int)((1-lines[i2 + 1]) * pm.getHeight())
            );
        }
        tex = new TextureRegion(new Texture(pm));
        ground = new MyActor(tex);
        ground.setBounds(400, 100, ground.getWidth()*10, ground.getHeight()*10);
        stage.addActor(ground);
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
