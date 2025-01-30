package Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;
import com.badlogic.gdx.graphics.Texture;

/**
 * a special type of enemy that doesn't affect sensor/physics
 * it has a simple animation and a body so that collisions can trigger
 */

public class Ghost extends Sprite {
    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;

    // Animation related fields
    private Animation<TextureRegion> floatAnimation;
    private Texture[] ghostTextures;
    private float stateTime;
    private static final float FRAME_DURATION = 0.1f;

    /**
     * creates a ghost in a specific location
     * sets up body and animations
     * @param world  the world the ghost is in
     * @param map    the tiledmap it references
     * @param bounds the ghosts bounds
     */
    public Ghost(World world, TiledMap map, Rectangle bounds) {
        this.world = world;
        this.map = map;
        this.bounds = bounds;
        this.stateTime = 0f;

        // Set the sprite's size and position
        setBounds(bounds.x / testGame.PPM, bounds.y / testGame.PPM,
            32 / testGame.PPM, 32 / testGame.PPM);
        setPosition(bounds.x / testGame.PPM, bounds.y / testGame.PPM);

        // Load animation
        loadFloatingAnimation();

        // Define the physics body
        defineGhost();
    }

    /**
     * loads ghosts animations and then loops it
     */
    private void loadFloatingAnimation() {
        // Load all 10 ghost frames
        ghostTextures = new Texture[10];
        TextureRegion[] frames = new TextureRegion[10];

        for (int i = 0; i < 10; i++) {
            String fileName = "ghost/Wispy" + (i + 1) + ".png";
            ghostTextures[i] = new Texture(Gdx.files.internal(fileName));
            frames[i] = new TextureRegion(ghostTextures[i]);
        }

        floatAnimation = new Animation<>(FRAME_DURATION, frames);
        floatAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Set initial texture
        setRegion(frames[0]);
    }

    /**
     * creates the ghosts body
     * doesn't block movements but collisions will happen
     */
    private void defineGhost() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() + getWidth()/2, getY() + getHeight()/2);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(getWidth()/2);

        fdef.shape = shape;
        fdef.isSensor = true;  // Makes the ghost not physically solid
        fixture = body.createFixture(fdef);
        fixture.setUserData(this);

        shape.dispose();
    }

    /**
     * updates ghost animations and repositions if needed
     * @param dt delta time since last frame
     */
    public void update(float dt) {
        stateTime += dt;

        // Update animation frame
        TextureRegion currentFrame = floatAnimation.getKeyFrame(stateTime, true);
        setRegion(currentFrame);

        // Update position
        setPosition(body.getPosition().x - getWidth()/2,
            body.getPosition().y - getHeight()/2);
    }

    /**
     * disposes of textures not in use
     */
    public void dispose() {
        // Dispose all textures
        for (Texture texture : ghostTextures) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }
}
