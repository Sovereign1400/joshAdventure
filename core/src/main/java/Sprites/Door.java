package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;

/**
 * this class represents the exit or entrance doors, locked or unlocked
 */
public class Door extends InteractiveTileObject {
    private boolean isOpen = false;
    private Texture doorTexture;
    private DoorType doorType;

    /**
     * creates a new door from tiledmap and puts it to Box2D
     * @param world   the Box2D world
     * @param map     the tiledmap that its from
     * @param bounds  the doors bounds
     * @param type    the door enum type
     */
    public Door(World world, TiledMap map, Rectangle bounds, DoorType type) {
        super(world, map, bounds);
        this.doorType = type;
        doorTexture = new Texture("door_2.png");

        // Create fixture and body
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth()/2) / testGame.PPM,
            (bounds.getY() + bounds.getHeight() / 2) / testGame.PPM);

        body = world.createBody(bdef);
        shape.setAsBox(bounds.getWidth() / 2 / testGame.PPM,
            bounds.getHeight() / 2 / testGame.PPM);
        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);  // Set user data here
        shape.dispose();
    }

    /**
     * lets the door do frame-by-frame logic if needed
     * unused
     */
    public void update() {
        // For animation updates
    }

    /**
     * door texture if not open yet
     * @param batch the spritebatch that is used
     */
    public void draw(SpriteBatch batch) {
        if (!isOpen) {
            float width = 48 / testGame.PPM;
            float height = 48 / testGame.PPM;
            batch.draw(doorTexture,
                bounds.getX() / testGame.PPM,
                bounds.getY() / testGame.PPM,
                width,
                height);
        }
    }

    /**
     * lets the player interact with the door and unlock it with a key
     * @param player the josh character
     */
    public void interact(Josh player) {
        if (doorType == DoorType.EXIT && player.hasKey()) {
            isOpen = true;
        }
    }

    /**
     * @return the door enum type for logic
     */
    public DoorType getDoorType() {
        return doorType;
    }

    /**
     * Disposes the door texture to free up resources
     */
    public void dispose() {
        doorTexture.dispose();
    }
}
