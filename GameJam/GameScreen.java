import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import com.badlogic.gdx.utils.Array;
// box2d imports
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;

import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.ContactImpulse;

// tilemap imports
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
//import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameScreen extends BaseScreen
{
    private Player player;
    private World physicsWorld;
    private HashMap<String,Room> rooms;
    private ArrayList<Box2DActor> removeList;

    TiledMap tiledMap;
    OrthographicCamera tiledCamera;
    OrthogonalTiledMapRenderer tiledMapRenderer;
    int[] tileLayer = {0};

    Portal destination;

    // calculate game world dimensions
    private int tileSize = 64;
    private int tileCountW = 10;
    private int tileCountH = 10;
    final int mapWidth  = tileSize * tileCountW;
    final int mapHeight = tileSize * tileCountH;

    public GameScreen(BaseGame g)
    {  super(g);  }

    public void create() 
    {        
        // not a platformer game, so gravity vector is (0,0)
        physicsWorld = new World(new Vector2(0,0), true);
        removeList = new ArrayList<Box2DActor>();
        rooms = new HashMap<String,Room>();
        destination = null;

        // initialize player

        player = new Player();
        Texture playerTex = new Texture( Gdx.files.internal("assets/general-single.png") );
        player.storeAnimation("default", playerTex); 
        mainStage.addActor(player);

        player.setDynamic();
        player.setShapeRectangle();
        player.setPhysicsProperties(1, 0.5f, 0.0f);
        player.setDamping(10);
        player.setMaxSpeedX(1);
        player.setFixedRotation();
        player.initializePhysics(physicsWorld);

        // initialize tilemap objects

        tiledCamera = new OrthographicCamera();
        tiledCamera.setToOrtho(false,viewWidth,viewHeight);
        tiledCamera.update();

        // load tilemaps. assume filename format "room-" + N + ".tmx"
        String[] fileIndices = {"1", "2", "3"};
        for (String fileIndex : fileIndices)
        {
            String fileName = "assets/room-" + fileIndex + ".tmx";
            TiledMap tm = new TmxMapLoader().load(fileName);
            Room rm = new Room(tm);
            MapProperties mp = tm.getProperties();
            String roomID = (String)mp.get("ID");
            rooms.put(roomID, rm);
        }

        Room startRoom = rooms.get("1");

        startRoom.activate( mainStage, physicsWorld );
        tiledMapRenderer = new OrthogonalTiledMapRenderer(startRoom.tiledMap);
        SpawnPoint startPoint = startRoom.getSpawnPoint( "Start" ); // make sure room has this!
        player.moveToOrigin(startPoint);
        player.updateBodyPosition();

        // contact listener manages interactions

        physicsWorld.setContactListener(
            new ContactListener() 
            {
                public void beginContact(Contact contact) 
                {   
                    Object objP = GameUtils.getContactObject(contact, Player.class);
                    if (objP == null)
                        return;

                    Player p = (Player)objP;
                    Object other = GameUtils.getOtherContactObject(contact, Player.class);

                    if (other instanceof Portal)
                    {
                        Portal portal = (Portal)other;
                        destination = portal;
                    }
                }

                public void endContact(Contact contact) 
                {  }

                public void preSolve(Contact contact, Manifold oldManifold) { }

                public void postSolve(Contact contact, ContactImpulse impulse) { }
            });

    }

    public void update(float dt) 
    {   
        removeList.clear();
        physicsWorld.step(1/60f, 6, 2);

        if (destination != null)
        {
            String roomID = destination.roomID;
            if ( !rooms.containsKey(roomID) )
                System.err.println("No such room: " + roomID);

            Room room = rooms.get( roomID );
            SpawnPoint sp = room.getSpawnPoint( destination.spawnID );
            // System.out.println( destination );

            // cleanup old room

            // clear contents of stage
            mainStage.clear();
            // remove bodies from world
            Array<Body> bodies = new Array<Body>();
            physicsWorld.getBodies(bodies);
            for (Body b: bodies)
                physicsWorld.destroyBody(b);

            // setup new room

            // add bodies to world and actors to stage
            room.activate( mainStage, physicsWorld );
            tiledMapRenderer.setMap( room.tiledMap );

            // reset player data
            mainStage.addActor(player);
            player.initializePhysics(physicsWorld);
            player.moveToOrigin(sp);
            player.updateBodyPosition();

            destination = null;
        }

        for (Box2DActor ba : removeList)
        {
            ba.destroy(); // removes from stage and any assigned parent list
            physicsWorld.destroyBody( ba.getBody() );
        }

        if( Gdx.input.isKeyPressed(Keys.LEFT) )
            player.applyForce( new Vector2(-3.0f, 0) );
        if( Gdx.input.isKeyPressed(Keys.RIGHT) )
            player.applyForce( new Vector2(3.0f, 0) );
        if( Gdx.input.isKeyPressed(Keys.UP) )
            player.applyForce( new Vector2(0, 3.0f) );
        if( Gdx.input.isKeyPressed(Keys.DOWN) )
            player.applyForce( new Vector2(0, -3.0f) );
    }

    // this is the gameloop. update, then render.
    public void render(float dt) 
    {
        uiStage.act(dt);

        // only pause gameplay events, not UI events
        if ( !isPaused() )
        {
            mainStage.act(dt);
            update(dt);
        }

        // render
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiledMapRenderer.setView(tiledCamera);
        tiledMapRenderer.render(tileLayer);

        mainStage.draw();
        uiStage.draw();
    }

    public boolean keyDown(int keycode)
    {
        if (keycode == Keys.P)    
            togglePaused();

        if (keycode == Keys.R)    
            game.setScreen( new GameScreen(game) );

        return false;
    }
}