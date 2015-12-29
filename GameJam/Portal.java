public class Portal extends Box2DActor
{
    public String roomID;
    public String spawnID;
    
    public Portal()
    {
        super();
        roomID = "MissingRoom";
        spawnID = "MissingSpawn";
    }
    
    public Portal(String r, String s)
    {
        super();
        roomID = r;
        spawnID = s;
    }
    
    public String toString()
    {  return "Portal to Room " + roomID + ", Spawn Point " + spawnID;  }
    
}