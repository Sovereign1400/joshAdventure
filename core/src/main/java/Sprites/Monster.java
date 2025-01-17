package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.some_example_name.testGame;

public class Monster extends InteractiveTileObject {
    private Texture texture; // Texture for rendering the monster
    private Vector2 position; // Position in the world
    private Vector2 velocity; // Velocity for movement
    private float speed; // Movement speed
    private boolean active; // Flag to determine if monster is active
    private Animation<TextureRegion> walkAnimation;

    public Monster(World world, TiledMap map, Rectangle bounds, Texture texture, float speed) {
        super(world, map, bounds);
        this.texture = texture;
        this.speed = speed;
        this.position = new Vector2(bounds.getX() / testGame.PPM, bounds.getY() / testGame.PPM);
        this.velocity = new Vector2(0, 0); // Initially stationary
        this.active = true;

        createWalkAnimation();
    }

    //Here is the walk animation
    private void createWalkAnimation() {
        // Suppose you have 4 frames: frame_1.png, frame_2.png, frame_3.png, frame_4.png.
        // Adjust frameCount to match the number of frames you have.
        int frameCount = 4;

        // Create arrays to store the textures and regions
        Texture[] frameTextures = new Texture[frameCount];
        TextureRegion[] walkFrames = new TextureRegion[frameCount];

        // Loop through each frame
        for (int i = 0; i < frameCount; i++) {
            // The file names are assumed to follow "frame_1.png", "frame_2.png", etc.
            String parent_path = "assets/knight/Run";
            String fileName = parent_path + "/" + "Run" + (i + 1) + ".png";

            // Load the texture for the current frame
            frameTextures[i] = new Texture(fileName);

            // Create a region from the texture
            walkFrames[i] = new TextureRegion(frameTextures[i]);
        }

        // Build the walk animation (0.1 seconds per frame, looping)
        walkAnimation = new Animation<>(0.1f, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    /**
     * Update the monster's behavior each frame.
     *
     * @param delta The time elapsed since the last frame.
     * @param targetPosition The position of the target (e.g., the player's position).
     */
    public void update(float delta, Vector2 targetPosition) {
        if (!active) return;

        // Calculate direction towards the target
        Vector2 direction = targetPosition.cpy().sub(position).nor();
        velocity.set(direction.scl(speed));

        // Update position based on velocity
        position.add(velocity.scl(delta));

        // Update the Box2D body position
        body.setTransform(position.x, position.y, 0);
    }

    /**
     * Render the monster.
     *
     * @param batch The SpriteBatch used to draw the texture.
     */
    public void render(SpriteBatch batch) {
        if (!active) return;

        batch.draw(texture, position.x * testGame.PPM - texture.getWidth() / 2, position.y * testGame.PPM - texture.getHeight() / 2);
    }

    /**
     * Disable the monster (e.g., if defeated).
     */
    public void deactivate() {
        active = false;
        world.destroyBody(body); // Remove the Box2D body
    }

    /**
     * Reactivate the monster (e.g., for respawning).
     */
    public void activate(Vector2 newPosition) {
        active = true;
        position.set(newPosition);
        body.setTransform(newPosition.x, newPosition.y, 0);
    }

    /**
     * Check if the monster is currently active.
     *
     * @return True if active, false otherwise.
     */
    public boolean isActive() {
        return active;
    }
}
