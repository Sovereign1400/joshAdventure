package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;

public class Door extends InteractiveTileObject {
    private boolean isOpen = false;
    private Texture doorTexture;
    private DoorType doorType;

    public Door(World world, TiledMap map, Rectangle bounds, DoorType type) {
        super(world, map, bounds);
        this.doorType = type;
        doorTexture = new Texture("door.png");

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

    public void update() {
        // For animation updates
    }

    public void draw(SpriteBatch batch) {
        if (!isOpen) {
            batch.draw(doorTexture,
                bounds.getX() / testGame.PPM,
                bounds.getY() / testGame.PPM,
                bounds.getWidth() / testGame.PPM,
                bounds.getHeight() / testGame.PPM);
        }
    }

    public void interact(Josh player) {
        if (doorType == DoorType.EXIT && player.hasKey()) {
            isOpen = true;
        }
    }

    public DoorType getDoorType() {
        return doorType;
    }

    public void dispose() {
        doorTexture.dispose();
    }
}
