import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.MapProperties;
// import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
// import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.badlogic.gdx.physics.box2d.World;

public class Room
{
    public TiledMap tiledMap;

    public String name;
    public ArrayList<Box2DActor> wallList;
    public ArrayList<Portal> portalList;
    public HashMap<String,SpawnPoint> spawnPoints;
    public ArrayList<Key> keyList;

    // constructor initializes Room data from TiledMap data
    public Room(TiledMap tm)
    {
        tiledMap = tm;

        wallList       = new ArrayList<Box2DActor>();
        portalList     = new ArrayList<Portal>();
        spawnPoints    = new HashMap<String,SpawnPoint>();
        keyList        = new ArrayList<Key>();

        MapObjects objects = tiledMap.getLayers().get("ObjectData").getObjects();
        for (MapObject object : objects) 
        {
            String name = object.getName();
            // all object data assumed to be stored as rectangles... but just in case
            if ( !(object instanceof RectangleMapObject) )
            {
                System.err.println("Unknown ObjectData object.");
                continue;
            }
            RectangleMapObject rmo = (RectangleMapObject)object;
            Rectangle r = rmo.getRectangle();
            MapProperties mp = object.getProperties(); // null if object doesn't store properties

            if ( name.equals("Portal") )
            {
                String roomID  = (String)mp.get("Room");
                String spawnID = (String)mp.get("Spawn");
                Portal p = new Portal( roomID, spawnID );
                p.setDimensions(r);
                p.setStatic();
                p.setSensor();
                p.setShapeRectangle();
                portalList.add(p);
            }
            else if ( name.equals("SpawnPoint") )
            {
                String spawnID = (String)mp.get("ID");
                SpawnPoint sp = new SpawnPoint();
                sp.setDimensions(r);
                sp.setOriginCenter();
                spawnPoints.put(spawnID, sp);
            }
            else if ( name.equals("Key") )
            {
                Key k = new Key();
                k.setPosition( r.x, r.y );
                k.setOriginCenter();
                k.setStatic();
                k.setSensor();
                k.setShapeRectangle();

                k.setParentList(keyList); // facilitate later removal
                keyList.add(k);
            }
            else
                System.err.println("Unknown tilemap object: " + name);
        }

        objects = tiledMap.getLayers().get("PhysicsData").getObjects();
        for (MapObject object : objects) 
        {
            // all physics data assumed to be stored as rectangles... but just in case
            if ( !(object instanceof RectangleMapObject) )
            {
                System.err.println("Unknown PhysicsData object.");
                continue;
            }
            RectangleMapObject rmo = (RectangleMapObject)object;
            Rectangle r = rmo.getRectangle();

            Box2DActor wall = new Box2DActor();
            wall.setDimensions(r);
            wall.setStatic();
            wall.setShapeRectangle();
            wallList.add(wall);
        }
    }

    public SpawnPoint getSpawnPoint(String ID)
    {  return spawnPoints.get(ID);  }

    public void activate(Stage ms, World w)
    {
        // To do... maybe...
        // - pass in TiledMapRenderer, and setMap here?
        // - clear contents of stage and world here instead of in main program?

        // nonvisual interactive elements only need to be added to the world

        for (Box2DActor wall : wallList)
            wall.initializePhysics(w);

        for (Portal p : portalList)
            p.initializePhysics(w);

        // visual interactive elements need to be added to the stage and world

        for (Key k : keyList)
        {
            ms.addActor(k);
            k.initializePhysics(w);
        }       
    }

}