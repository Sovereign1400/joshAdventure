package Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;

/**
 * a generic parent class for tile objects
 * handles creation of a static Box2D body (rectangle)
 */
public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;

    /**
     * creates a tile object at a specific location
     * @param world  the box2d world for physics
     * @param map    the tiledmap for reference
     * @param bounds the bounds of this object
     */
    public InteractiveTileObject(World world, TiledMap map, Rectangle bounds){
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        // This creates ground object bodies and fixtures.
        bdef.position.set((bounds.getX() + bounds.getWidth()/2) / testGame.PPM, (bounds.getY() + bounds.getHeight() / 2) / testGame.PPM);

        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth() / 2 / testGame.PPM, bounds.getHeight() / 2 / testGame.PPM);
        fdef.shape = shape;
        body.createFixture(fdef);
    }

    /**
     * @return the x coordinates of it
     */
    public float getBoundsX() {
        return bounds.x;
    }

    /**
     * @return the y coordinates of it
     */
    public float getBoundsY() {
        return bounds.y;
    }

    /**
     * @return the tiles bounds (width)
     */
    public float getBoundsWidth() {
        return bounds.width;
    }

    /**
     * @return the tiles bounds (height)
     */
    public float getBoundsHeight() {
        return bounds.height;
    }
}
