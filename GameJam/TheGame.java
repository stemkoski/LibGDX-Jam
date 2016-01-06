import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.graphics.g2d.NinePatch;
public class TheGame extends BaseGame
{
    public void create() 
    {  
        // initialize resources common to multiple screens
        
        // Bitmap font
        BitmapFont uiFont = new BitmapFont(Gdx.files.internal("assets/cooper.fnt"));
        uiFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        uiFont.getData().setScale(0.5f);
        skin.add("uiFont", uiFont);

       

        // Dialog background
        Texture dialogTex = new Texture(Gdx.files.internal("assets/dialog.png"));
        skin.add("dialogTex", new NinePatch(dialogTex, 16,16,16,16));
        
         // Label style
        LabelStyle uiLabelStyle = new LabelStyle(uiFont, Color.WHITE);
        // using newDrawable allows color tint, vs getDrawable
        // uiLabelStyle.background = skin.newDrawable("dialogTex", new Color(1.0f,1.0f,1.0f,0.8f));
        skin.add("uiLabelStyle", uiLabelStyle);
        
        // initialize and set start screen 
        GameScreen gs = new GameScreen(this);
        setScreen( gs );
    }
}
