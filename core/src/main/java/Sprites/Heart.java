package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;

/**
 * a heart potion that gives player 1 health if not at max
 * disappears when collected but only collectible if not max health
 */
public class Heart extends Sprite {
    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected Texture heartTexture;
    private boolean collected = false;
    private boolean toBeDestroyed = false;

    /**
     * constructs heart potion at a specific location
     * @param world  the box2d world for physics
     * @param map    the tiled map it references
     * @param bounds the bounds of the heart
     */
    public Heart(World world, TiledMap map, Rectangle bounds) {
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        setBounds(bounds.x / testGame.PPM, bounds.y / testGame.PPM,
            16 / testGame.PPM, 16 / testGame.PPM);

        setPosition(bounds.x / testGame.PPM, bounds.y / testGame.PPM);

        heartTexture = new Texture("pickups/hp+1.png");
        setRegion(heartTexture);
        defineHeart();
    }

    /**
     * makes it so that the heart doesn't block movement
     */
    private void defineHeart() {
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
     * checks if the heart should be destroyed
     */
    public void update() {
        if (toBeDestroyed && !world.isLocked()) {
            world.destroyBody(body);
            collected = true;
            toBeDestroyed = false;
        }
    }

    /**
     * called when player collides with a heart
     * only gets destroyed and consumed if health is below 3
     * @param josh the player in question
     */
    public void onCollect(Josh josh) {
        if (!collected) {
            if (josh.getHealth() < 3) {
                toBeDestroyed = true;
                collected = true;
            }
        }

    }

    /**
     * @return true if heart is collected
     */
    public boolean isCollected() {
        return collected;
    }
}
