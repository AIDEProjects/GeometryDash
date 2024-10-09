package com.goldsprite.libgdxEngine.core.scene2d;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class NormalButton extends TextButton {
    static Texture btn_up = new Texture("UI_Button_Back_Up.png");
    static Texture btn_down = new Texture("UI_Button_Back_Down.png");
    static TextureRegionDrawable btn_up_drawable = new TextureRegionDrawable(new TextureRegion(btn_up));
    static TextureRegionDrawable btn_down_drawable = new TextureRegionDrawable(new TextureRegion(btn_down));
    
    public NormalButton(String txt, TextButtonStyle style){
        super(txt, style);
        
        init(txt, style);
    }

    private void init(String txt, TextButtonStyle style) {
        TextButton playButton = new TextButton("Play", style);
    }
    
    public static TextButtonStyle newDefaultStyle(){
        BitmapFont fnt = new BitmapFont();
        fnt.getData().lineHeight = 100;
        fnt.getData().scale(3);
        TextButtonStyle style = new TextButtonStyle();
        style.font = fnt;
        style.up = new TextureRegionDrawable(new TextureRegion(btn_up));
        style.down = new TextureRegionDrawable(new TextureRegion(btn_down));
        return style;
    }
    
}
