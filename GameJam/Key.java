import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
public class Key extends Box2DActor
{
    // TODO: add ID? color for tinting? (must match door to unlock; could double as ID)
    public Key()
    {  
        super();  
        Texture keyTex = new Texture(Gdx.files.internal("assets/key.png"));
        keyTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        storeAnimation( "default", keyTex );
    }
    
    public Key clone()
    {
        Key newbie = new Key();
        newbie.copy( this );
        return newbie;
    }
}