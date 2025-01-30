package Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;

/**
 * an item with the sole purpose of giving points when collected
 * has a body that wont interfere with movement
 * removed upon pickup
 */
public class Scoreup extends Sprite{
    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected Texture scoreupTexture;
    private boolean collected = false;
    private boolean toBeDestroyed = false;

    /**
     *creates the scoreup at a specific location
     *
     * @param world  the box2d world for physics
     * @param map    tiledmap for reference
     * @param bounds the bounds of the scoreup
     */
    public Scoreup(World world, TiledMap map, Rectangle bounds) {
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        setBounds(bounds.x / testGame.PPM, bounds.y / testGame.PPM,
            16 / testGame.PPM, 16 / testGame.PPM);

        setPosition(bounds.x / testGame.PPM, bounds.y / testGame.PPM);

        scoreupTexture = new Texture("pickups/scoreup.png");
        setRegion(scoreupTexture);
        defineScoreup();
    }

    /**
     * defines a small circle body that won't interfere with movements (static)
     */
    private void defineScoreup() {
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
     * if collected and world isn't locked, removes the box2d body of the scoreup
     */
    public void update() {
        if (toBeDestroyed && !world.isLocked()) {
            world.destroyBody(body);
            collected = true;
            toBeDestroyed = false;
        }
    }

    /**
     * called on pickup
     * gets the scoreup ready to be destroyed if collected
     */
    public void onCollect() {
        if (!collected) {
            toBeDestroyed = true;
        }
    }

    /**
     * @return true if its picked up
     */
    public boolean isCollected() {
        return collected;
    }
}
