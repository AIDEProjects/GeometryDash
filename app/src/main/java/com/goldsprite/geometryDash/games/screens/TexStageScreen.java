package com.goldsprite.geometryDash.games.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.goldsprite.geometryDash.MainActivity;
import com.goldsprite.libgdxEngine.core.math.CompositeRect;
import com.goldsprite.libgdxEngine.core.math.Line;
import com.goldsprite.libgdxEngine.core.math.Rectangle;
import com.goldsprite.libgdxEngine.core.math.Vector2;
import com.goldsprite.libgdxEngine.core.scene2d.GsGame;
import com.goldsprite.libgdxEngine.core.scene2d.LinkScreen;
import com.goldsprite.libgdxEngine.core.scene2d.actors.MyActor;
import java.io.PrintWriter;
import java.io.StringWriter;

public class TexStageScreen extends LinkScreen {
    ShapeRenderer shapeRender = new ShapeRenderer();
    Stage stage=new Stage();

    private TextureRegion tex;

    private MyActor player, playerCollDeathZone;

    private int dirRight;
    private float moveVel=300, velX;

    private MyActor ground;

    private boolean touchScreen;

    private CompositeRect crec;

    private Vector2 dir = new Vector2(0, 0);


    public TexStageScreen(GsGame game) {
        super(game);

        init();
    }

    private void init() {
        Gdx.input.setInputProcessor(stage);
        stage.addListener(new MyInputListener());

        createPlayer();
        createPlayerCollDeathZone();

        createGround();
    }

    private void createPlayer() {
        Pixmap pm = new Pixmap(12, 10, Pixmap.Format.RGBA8888);
        pm.setColor(Color.GREEN);
        pm.drawRectangle(0, 0, 12, 10);
        tex = new TextureRegion(new Texture(pm));
        player = new MyActor(tex);
        player.setBounds(1518, 710, player.getWidth() * 12, player.getHeight() * 10);
        stage.addActor(player);

    }

    public void createPlayerCollDeathZone() {
        int dzWidth = (int)player.getWidth();
        int dzHeight = (int)player.getHeight();
        Pixmap pm = new Pixmap(dzWidth, dzHeight, Pixmap.Format.RGBA8888);
        pm.setColor(Color.BROWN);
        pm.drawRectangle(0, 0, dzWidth, dzHeight);
        tex = new TextureRegion(new Texture(pm));
        playerCollDeathZone = new MyActor(tex);
        playerCollDeathZone.setSize(dzWidth, dzHeight);
        playerCollDeathZone.setPosition(player.getX(Align.center), player.getY(Align.center), Align.center);
        stage.addActor(playerCollDeathZone);
        updateRecCollDeathZoneVal();
    }

    private void createGround() {

        Pixmap pm = new Pixmap(160, 50, Pixmap.Format.RGBA8888);
        pm.setColor(Color.BLUE);
        /*
         float[] lines={
         0, 0, 
         1, 0, 
         1, 1, 
         0, 1
         };
         */
        int pmW=pm.getWidth(), pmH=pm.getHeight();
        float[] lines={
            0, 0, 
            0.2f, 0, 
            0.2f, 0.3f, 
            0.4f, 0.3f, 
            0.4f, 0, 
            1, 0, 
            1, 0.3f, 
            0.8f, 0.3f, 
            0.8f, 0.2f, 
            0.6f, 0.2f, 
            0.6f, 0.7f,
            0.8f, 0.7f, 
            0.8f, 0.6f, 
            1, 0.6f, 
            1, 0.8f, 
            0.7f, 0.8f, 
            0.7f, 1, 
            0.3f, 1, 
            0.3f, 0.8f, 
            0, 0.8f
        };
        float[] edges = new float[lines.length];
        for (int i=0,i2=2;i < lines.length;i += 2, i2 = (i + 2) % lines.length) {
            edges[i] = lines[i] * pmW;
            edges[i + 1] = lines[i + 1] * pmH;
            edges[i2] = lines[i] * pmW;
            edges[i2 + 1] = lines[i2 + 1] * pmH;
        }
        for (int i=0,i2=2;i < edges.length;i += 2, i2 = (i + 2) % edges.length) {
            Line edge = new Line(edges[i], edges[i + 1], edges[i2], edges[i2 + 1]);
            edge = fixOutEdge(edge, -1);
            edges[i] = edge.start.x;
            edges[i + 1] = edge.start.y;
            edges[i2] = edge.end.x;
            edges[i2 + 1] = edge.end.y;
        }
        for (int i=0,i2=2;i < edges.length;i += 2, i2 = (i + 2) % edges.length) {
            pm.drawLine((int)edges[i], pmH - (int)edges[i + 1], (int)edges[i2], pmH - (int)edges[i2 + 1]);
        }
        tex = new TextureRegion(new Texture(pm));
        ground = new MyActor(tex);
        ground.setBounds(350, 200, ground.getWidth() * 10, ground.getHeight() * 10);
        crec = new CompositeRect(lines, ground.getX(), ground.getY(), ground.getWidth(), ground.getHeight());
        stage.addActor(ground);
    }

    private Line fixOutEdge(Line edge, int dir) {
        Vector2 normalVec = edge.normalVector();
        Vector2 normal = normalVec.normalize();


        if (normal.equals(Vector2.down)) {
            edge.add(Vector2.up);
        }
        if (normal.equals(Vector2.right)) {
            edge.add(Vector2.left);
        }
        return edge;
    }

    @Override
    public void render(float delta) {
        setClearColor(1, 1, 1, 1);
        super.render(delta);

        updateLogic();

        stage.act();
        stage.draw();

    }

    float oldcollDeathZoneVal=0;
    private void updateLogic() {
        
        updateRecCollDeathZoneVal();

        Rectangle rec1 = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        //单步碰撞测试
        if (touchScreen) {
            if(MainActivity.stepTest)touchScreen = false;
            float delta = 1 / 60f * 4f * (MainActivity.stepTest?2 : 0.3f);

            rec1.velocity = new Vector2(moveVel * 0.8f * dir.x, moveVel * dir.y).multiply(5).multiply(MainActivity.playerVelVal);

            boolean isColl = -1 != rec1.resolveCollisionWithCompositeRect(delta, crec);
            Vector2 oldPos=rec1.position;
            Vector2 perTrans;//每次位移量

            rec1.move(delta);
            player.setPosition(rec1.position.x, rec1.position.y);
            playerCollDeathZone.setPosition(player.getX(Align.center), player.getY(Align.center), Align.center);
            perTrans = rec1.position.subtract(oldPos);

        }
    }

    public void updateRecCollDeathZoneVal(){
        float size = Math.min(player.getWidth(), player.getHeight());
        Rectangle.collDeathZone = (MainActivity.collDeathZone) * size;//这里取大，取小总是有部分会在外面
        if (MainActivity.collDeathZone != oldcollDeathZoneVal) {
            oldcollDeathZoneVal = MainActivity.collDeathZone;
            updatePlayerCollDeathZoneSize();
        }
    }
    Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    Texture texture = new Texture(pm);
    TextureRegion region2 = new TextureRegion();
    private void updatePlayerCollDeathZoneSize() {
        float dz = Rectangle.collDeathZone;

        int dzWidth = (int)(player.getWidth() - dz);
        int dzHeight = (int)(player.getHeight() - dz);
        pm = new Pixmap(dzWidth, dzHeight, Pixmap.Format.RGBA8888);
        pm.setColor(Color.BROWN);
        pm.drawRectangle(0, 0, dzWidth, dzHeight);
        texture = new Texture(pm);
        region2.setRegion(texture);
        playerCollDeathZone.setRegion(region2);
        playerCollDeathZone.setSize(dzWidth, dzHeight);
        playerCollDeathZone.setPosition(player.getX(Align.center), player.getY(Align.center), Align.center);
    }


    public class MyInputListener extends InputListener {

        //原点在左下角
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            handlePlayerInput(x, y);
            touchScreen = true;
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            dirRight = 0;
            touchScreen = false;
            dir = dir.multiply(0);
            super.touchUp(event, x, y, pointer, button);
        }

        public void handlePlayerInput(float x, float y) {
            //下方
            if (y < Gdx.graphics.getHeight() / 2f) {
                //左或右
                dirRight = x > Gdx.graphics.getWidth() / 2f ?1: -1;
                dir.x = x > Gdx.graphics.getWidth() / 2f ?1: -1;
            } else {
                //上方
                System.out.println("jump");
                dir.y = x > Gdx.graphics.getWidth() / 2f ?1: -1;
            }
        }

    }


}
