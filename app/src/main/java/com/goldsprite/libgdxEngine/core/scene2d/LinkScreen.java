package com.goldsprite.libgdxEngine.core.scene2d;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.Screen;
import java.io.StringWriter;
import java.io.PrintWriter;

public class LinkScreen extends ScreenAdapter {
    private GsGame game;
    public GsGame getGame() {return game;}
    private Stage uiStage;
    public Stage getUIStage() {return uiStage;}
    private ShapeRenderer shapeRenderer;
    private static TransitionEffect transitionEffect;
    public float delta;

    public LinkScreen(GsGame game) {
        this.game = game;
        this.uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(uiStage);
        shapeRenderer = new ShapeRenderer();
        if (transitionEffect == null)
            transitionEffect = new TransitionEffect();
        else
            transitionEffect.reset();
    }
    public void setGame(GsGame game) {this.game = game;}

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    public void startTransition(Class clazz) {
        transitionEffect.transitioning = 1;
        transitionEffect.transitionClass = clazz;
    }
    LinkScreen nextScreen;
    public void startTransition(LinkScreen screen) {
        nextScreen = screen;
        startTransition(screen.getClass());
    }

    float[] clearColor={1, 1, 1, 1};
    protected void setClearColor(float c0, float c1, float c2, float c3) {
        clearColor[0] = c0;
        clearColor[1] = c1;
        clearColor[2] = c2;
        clearColor[3] = c3;
    }
    @Override
    public void render(float delta) {
        this.delta = delta;
        Gdx.gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]); // 清屏白色背景
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        getUIStage().act(delta);
        getUIStage().draw();

        if (transitionEffect.transitioning == -1) {
            transitionEffect.blackScreenHeight += transitionEffect.transitionSpeed * delta / 2f;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.rect(0, transitionEffect.blackScreenHeight, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - transitionEffect.blackScreenHeight);
            shapeRenderer.end();
            if (transitionEffect.blackScreenHeight > Gdx.graphics.getHeight()) {
                transitionEffect.blackScreenHeight = 0;
                transitionEffect.transitioning = 0;
            }
        }
        if (transitionEffect.transitioning == 1) {
            transitionEffect.blackScreenHeight += transitionEffect.transitionSpeed * delta;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.rect(0, 0, transitionEffect.blackScreenHeight, Gdx.graphics.getHeight());
            shapeRenderer.end();
            if (transitionEffect.blackScreenHeight > Gdx.graphics.getWidth() * 2) {
                cgScreen();
            }
        }
    }

    public void cgScreen() {
        getGame().setScreen(nextScreen);
        transitionEffect.reset();
    }


    @Override
    public void dispose() {
        uiStage.dispose();
        shapeRenderer.dispose();
    }


    public static class TransitionEffect {
        public float blackScreenHeight = 0; // 黑幕的宽度
        public int transitioning = -1;  //转换状态 012
        public Class transitionClass;
        public float transitionSpeed = 3000;


        public void reset() {
            blackScreenHeight = 0;
            transitioning = -1;
        }
    }


}
