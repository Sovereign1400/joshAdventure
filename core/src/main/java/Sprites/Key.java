package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;

/**
 * a key! when collected we get a key and it disappears
 * it can be used to use exit doors
 */
public class Key extends Sprite {
    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected Texture keyTexture;
    private boolean collected = false;
    private boolean toBeDestroyed = false;

    /**
     * creates a key that wont interfere with movement (static)
     * placed at the given rectangle area
     * @param world  the box2d world for physics
     * @param map    tiledmap reference
     * @param bounds the keys bounds
     */
    public Key(World world, TiledMap map, Rectangle bounds) {
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        setBounds(bounds.x / testGame.PPM, bounds.y / testGame.PPM,
            16 / testGame.PPM, 16 / testGame.PPM);

        setPosition(bounds.x / testGame.PPM, bounds.y / testGame.PPM);

        keyTexture = new Texture("pickups/key.png");
        setRegion(keyTexture);
        defineKey();
    }

    /**
     * this builds the sensor fixture so that it doesn't interfere with movement
     */
    private void defineKey() {
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
     * if collected removes the box2d body of the key
     */
    public void update() {
        if (toBeDestroyed && !world.isLocked()) {
            world.destroyBody(body);
            collected = true;
            toBeDestroyed = false;
        }
    }

    /**
     * called upon the collection of the key
     * gets the key ready for removel
     */
    public void onCollect() {
        if (!collected) {
            toBeDestroyed = true;
        }
    }

    /**
     * @return true if key is collected
     */
    public boolean isCollected() {
        return collected;
    }
}
