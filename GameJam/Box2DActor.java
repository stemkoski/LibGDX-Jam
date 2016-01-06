import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;


public class Box2DActor extends AnimatedActor
{
    // body definition - used to initialize body
    // body definition data: position, angle, 
    //   linearVelocity, angularVelocity,
    //   type (static, dynamic),
    //   fixedRotation (can this object rotate?)

    protected BodyDef bodyDef;

    protected Body body;

    // fixture definition - used to initialize fixture
    // fixture data: shape, density, friction, restitution (0 to 1)
    //  *** weight is calculated via density*area

    // fixture - attached to body

    protected FixtureDef fixtureDef;

    protected Float maxSpeed;
    protected Float maxSpeedX;
    protected Float maxSpeedY;
    
    public Box2DActor()
    {
        body       = null;
        bodyDef    = new BodyDef();
        fixtureDef = new FixtureDef();

        maxSpeed = null;
        maxSpeedX = null;
        maxSpeedY = null; // hundreds of pixels per second
    }

    public void setStatic()
    {  bodyDef.type = BodyType.StaticBody;  } 

    public void setDynamic()
    {  bodyDef.type = BodyType.DynamicBody;  }

    public void setFixedRotation()
    {  bodyDef.fixedRotation = true;  }
    
    // set damping (automatic deceleration; for top-down game, simulates friction with the floor)
    public void setDamping(float f)
    {  bodyDef.linearDamping = f;  }

    // will only register overlaps; object is not solid.
    public void setSensor()
    {  fixtureDef.isSensor = true;  }
    
    public void setShapeRectangle()
    {
        setOriginCenter();
        // position must be centered
        bodyDef.position.set( (getX() + getOriginX()) / 100, (getY() + getOriginY())/100 );
        PolygonShape rect = new PolygonShape();
        rect.setAsBox( getWidth()/200, getHeight()/200 );
        fixtureDef.shape = rect;
        // rect.dispose();
    }

    public void setShapeCircle()
    {
        setOriginCenter();
        // position must be centered
        bodyDef.position.set( (getX() + getOriginX()) / 100, (getY() + getOriginY())/100 );
        // Create a circle shape and set its radius to 6
        CircleShape circ = new CircleShape();
        circ.setRadius( getWidth()/200 );
        fixtureDef.shape = circ;
        //circ.dispose();
    }

    public void setPhysicsProperties(float density, float friction, float restitution)
    {
        fixtureDef.density     = density;
        fixtureDef.friction    = friction;
        fixtureDef.restitution = restitution;
    }

    public void setMaxSpeed(float f)
    {  maxSpeed = f;  }

    public void setMaxSpeedX(float f)
    {  maxSpeedX = f;  }

    public void setMaxSpeedY(float f)
    {  maxSpeedY = f;  }

    public Body getBody()
    {  return body;  }

    // uses data to initialize object and add to world
    public void initializePhysics(World w)
    {
        // initialize a body; automatically added to world
        body = w.createBody(bodyDef);
        
        // initialize a Fixture and attach it to the body
        //  don't need to store it?
        Fixture f = body.createFixture(fixtureDef);
        f.setUserData("main");

        // store reference to this, so can access from collision
        body.setUserData(this);
    }

    // once game is running...
    public void applyForce(Vector2 force)
    {  body.applyForceToCenter(force, true);  }

    public void applyImpulse(Vector2 impulse)
    {  body.applyLinearImpulse(impulse, body.getPosition(), true);  }

    public Vector2 getVelocity()
    {  return body.getLinearVelocity(); }

    public float getSpeed()
    {  return getVelocity().len();  }

    public void setVelocity(float vx, float vy)
    {  body.setLinearVelocity(vx,vy);  }

    public void setVelocity(Vector2 v)
    {  body.setLinearVelocity(v);  }

    public void setSpeed(float s)
    {  setVelocity( getVelocity().setLength(s) );  }

    public float getMotionAngle()
    {  return (float)Math.atan2( getVelocity().y, getVelocity().x );  }
    
    public String getMotionName()
    {
        float a = getMotionAngle();
        if (-0.25f * MathUtils.PI <= a && a <= 0.25f * MathUtils.PI)
            return "right";
        else if (0.25f * MathUtils.PI < a && a < 0.75f * MathUtils.PI)
            return "up";
        else if (-0.75f * MathUtils.PI < a && a < -0.25f * MathUtils.PI)
            return "down";
        else // (a > 0.75f * MathUtils.PI || a < -0.75f * MathUtils.PI)
            return "left";
    }
    
    public void act(float dt) 
    {
        super.act(dt);

        // cap max speeds, if they have been set
        if (maxSpeedX != null)
        {
            Vector2 v = getVelocity();
            v.x = MathUtils.clamp(v.x, -maxSpeedX, maxSpeedX);
            setVelocity(v);
        }
        if (maxSpeedY != null)
        {
            Vector2 v = getVelocity();
            v.y = MathUtils.clamp(v.y, -maxSpeedY, maxSpeedY);
            setVelocity(v);
        }
        if (maxSpeed != null)
        {
            float s = getSpeed();
            if (s > maxSpeed)
                setSpeed(maxSpeed);
        } 

        // update image data, position&rotation, based on physics data
        Vector2 center = body.getWorldCenter();        
        setPosition( 100*center.x - getOriginX(), 100*center.y - getOriginY() );

        float a = body.getAngle();      // angle in radians
        setRotation( a * 360 / 6.28f ); // convert to degrees
    }

    public Box2DActor clone()
    {
        Box2DActor newbie = new Box2DActor();
        newbie.copy( this ); 
        // only copies AnimatedActor data
        // because we don't want to share physics data
        return newbie;
    }
    
    public void updateBodyPosition()
    {
        body.setTransform( (getX() + getOriginX()) / 100, (getY() + getOriginY())/100, 0 );
    }

}