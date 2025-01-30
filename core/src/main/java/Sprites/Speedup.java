package Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;

/**
 * a speed boost item that grants a time limited speed boost
 * disappears when collected and has a static body
 */
public class Speedup extends Sprite {
    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected Texture speedupTexture;
    private boolean collected = false;
    private boolean toBeDestroyed = false;

    /**
     * creates a speedup at a specific location
     * @param world  the box2d world for physics
     * @param map    tiledmap for reference
     * @param bounds bounds of the speedup
     */
    public Speedup(World world, TiledMap map, Rectangle bounds) {
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        setBounds(bounds.x / testGame.PPM, bounds.y / testGame.PPM,
            16 / testGame.PPM, 16 / testGame.PPM);

        setPosition(bounds.x / testGame.PPM, bounds.y / testGame.PPM);

        speedupTexture = new Texture("pickups/speedup.png");
        setRegion(speedupTexture);
        defineSpeedup();
    }

    /**
     * small circle static body/sensor for the speed pickup
     */
    private void defineSpeedup() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() + getWidth()/2, getY() + getHeight()/2);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(getWidth()/2);

        fdef.shape = shape;
        fdef.isSensor = true;
        fixture = body.createFixture(fdef);
        fixture.setUserData(this);

        shape.dispose();
    }

    /**
     * if collected and the world isn't locked, destroys speedups body
     */
    public void update() {
        if (toBeDestroyed && !world.isLocked()) {
            world.destroyBody(body);
            collected = true;
            toBeDestroyed = false;
        }
    }

    /**
     * called when speedup is collected
     * gets speedup ready for destruction
     */
    public void onCollect() {
        if (!collected) {
            toBeDestroyed = true;
        }
    }

    /**
     * @return true if speedup is collected
     */
    public boolean isCollected() {
        return collected;
    }
}
