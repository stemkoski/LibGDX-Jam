import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;

public class ParticleActor extends Actor
{
    private ParticleEffect pe;

    public ParticleActor()
    {
        super();
        pe = new ParticleEffect();
    }

    public void load(String pfxFile, String imageDirectory)
    {  pe.load(Gdx.files.internal(pfxFile), Gdx.files.internal(imageDirectory));  }

    public void start()
    {  pe.start();  }

    // pauses continuous emitters
    public void stop()
    {  pe.allowCompletion();  }

    public boolean isRunning()
    {  return !pe.isComplete();  }
    
    public void setPosition(float px, float py)
    {
        for (ParticleEmitter e : pe.getEmitters() )
            e.setPosition(px, py);
    }

    public void act(float dt)
    {
        super.act( dt );
        pe.update( dt );
        if ( pe.isComplete() && !pe.getEmitters().first().isContinuous() )
        {
            pe.dispose();
            this.remove();
        }
    }

    public void draw(Batch batch, float parentAlpha) 
    {   pe.draw(batch);  }

    public ParticleActor clone()
    {  
        ParticleActor newbie = new ParticleActor();
        newbie.pe = new ParticleEffect(this.pe);
        return newbie;
    }
}