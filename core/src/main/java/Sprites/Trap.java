package Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;

/**
 * a few holes that spit out fire, will damage player if they and the trap overlaps
 * uses sensor fixture to detect said collision
 */
public class Trap extends Sprite {
    private World world;
    public Body b2body;
    private String parent_path = "traps/";
    private float stateTime;

    // Animation
    private Animation<TextureRegion> trapAnimation;
    private Texture[] trapTextures;

    /**
     * creates a trap at a specific location
     * @param world  the box2d world for physics
     * @param x      x position
     * @param y      y position
     */
    public Trap(World world, float x, float y) {
        this.world = world;
        stateTime = 0;

        // Load animation
        loadTrapAnimation();

        // Set initial frame
        TextureRegion firstFrame = trapAnimation.getKeyFrame(0);
        float scale = 0.5f;  // Adjust scale as needed
        setBounds(0, 0,
            (firstFrame.getRegionWidth() * scale) / testGame.PPM,
            (firstFrame.getRegionHeight() * scale) / testGame.PPM);
        setRegion(firstFrame);

        defineTrap(x, y);
    }

    /**
     * loads trap animation frames from the traps folder
     * sets up a loop if successful
     */
    private void loadTrapAnimation() {
        try {
            int frameCount = 13; // Adjust based on your actual frame count
            trapTextures = new Texture[frameCount];
            TextureRegion[] frames = new TextureRegion[frameCount];

            for (int i = 0; i < frameCount; i++) {
                String fileName = parent_path + "trap_" + (i) + ".png";
                if (Gdx.files.internal(fileName).exists()) {
                    trapTextures[i] = new Texture(Gdx.files.internal(fileName));
                    frames[i] = new TextureRegion(trapTextures[i]);
                } else {
                    System.err.println("Failed to load trap frame: " + fileName);
                    // Use first frame as fallback if it exists
                    if (i > 0 && trapTextures[0] != null) {
                        trapTextures[i] = trapTextures[0];
                        frames[i] = new TextureRegion(trapTextures[0]);
                    }
                }
            }

            trapAnimation = new Animation<>(0.1f, frames); // Adjust timing as needed
            trapAnimation.setPlayMode(Animation.PlayMode.LOOP);
        } catch (Exception e) {
            System.err.println("Error loading trap animation: " + e.getMessage());
            // Create a default texture if loading fails
            Texture defaultTexture = new Texture(Gdx.files.internal("traps/trap_1.png"));
            TextureRegion[] defaultFrame = new TextureRegion[]{new TextureRegion(defaultTexture)};
            trapAnimation = new Animation<>(0.1f, defaultFrame);
            trapTextures = new Texture[]{defaultTexture};
        }
    }

    /**
     * the trap is a static body with small circle (collision detection)
     * @param x x position
     * @param y y position
     */
    private void defineTrap(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x / testGame.PPM, y / testGame.PPM);
        bdef.type = BodyDef.BodyType.StaticBody; // Static because trap doesn't move
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / testGame.PPM);

        fdef.shape = shape;
        fdef.isSensor = true; // Makes it trigger collision but not affect physics
        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    /**
     * updates traps animation and keeps it in sync
     * @param dt delta time since last frame
     */
    public void update(float dt) {
        stateTime += dt;

        // Update animation frame
        if (trapAnimation != null) {
            TextureRegion frame = trapAnimation.getKeyFrame(stateTime, true);
            if (frame != null) {
                setRegion(frame);
            }
        }

        // Update position (static, but needed for rendering)
        setPosition(
            b2body.getPosition().x - getWidth() / 2,
            b2body.getPosition().y - getHeight() / 2
        );
    }

    /**
     * cleans up traps frames to free up resources
     */
    public void dispose() {
        if (trapTextures != null) {
            for (Texture texture : trapTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
    }
}
