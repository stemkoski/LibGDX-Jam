import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
public class Launcher
{
    public static void main ()
    {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        
        // change configuration settings
        config.width = 640;
        config.height = 640;
        config.title = "The Game";
        
        TheGame myProgram = new TheGame();
        LwjglApplication launcher = new LwjglApplication( myProgram, config );
    }
}