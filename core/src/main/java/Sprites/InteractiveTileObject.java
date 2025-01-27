package Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;

public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;

    public InteractiveTileObject(World world, TiledMap map, Rectangle bounds){
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        // This creates ground object bodies and fixtures.
//        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth()/2) / testGame.PPM, (bounds.getY() + bounds.getHeight() / 2) / testGame.PPM);

        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth() / 2 / testGame.PPM, bounds.getHeight() / 2 / testGame.PPM);
        fdef.shape = shape;
        body.createFixture(fdef);
    }

    public float getBoundsX() {
        return bounds.x;
    }

    public float getBoundsY() {
        return bounds.y;
    }

    public float getBoundsWidth() {
        return bounds.width;
    }

    public float getBoundsHeight() {
        return bounds.height;
    }
}
