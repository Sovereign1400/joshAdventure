package Sprites;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;

public class Josh extends Sprite {
    public World world;
    public Body b2body;
    private Texture playerTexture;
    private String parent_path = "roman/soldier/";
    private float spawnX, spawnY; // spawn coordinates for Josh

    // ---------------------------
    // 1) Different animations
    // ---------------------------
    private Animation<TextureRegion> standAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> runAnimation;

    // If you have more stances, create more Animation fields, e.g. attackAnimation, etc.
    // We'll keep references to the individual textures so we can dispose them later
    private Texture[] standTextures;
    private Texture[] walkTextures;
    private Texture[] runTextures;

    // The current stance
    public enum Stance {
        STAND,
        WALK,
        RUN,
        HURT,
        ATTACK,
        DEATH
    }

    // hurt animation
    private Animation<TextureRegion> hurtAnimation;
    private Texture[] hurtTextures;
    private float hurtTimer = 0;
    private float hurtDuration = 0.5f;
    private boolean isHurt = false;

    // dead stance
    private boolean isDead = false;
    private Animation<TextureRegion> deathAnimation;
    private Texture[] deathTextures;
    private float deathTimer = 0;
    private float deathDuration = 2f;

    private Stance currentStance;
    private float stateTime;
    public boolean facingLeft = true;


    // Heart health system
    private int health = 8;

    // Speedup system attributes
    private float baseMovespeed;
    private float movespeed;
    private boolean speedBoostActive = false;
    private float speedBoostTimer = 0;
    private float speedBoostDuration = 10f; // 10 seconds for speedup pickups

    // Key
    private boolean hasKey = false;

    public Josh(World world, float spawnX, float spawnY){
        // Initialize the player's texture
        playerTexture = new Texture(parent_path + "idle" + "/" + "idle_knight_1.png");
        float scale = 0.5f;

        this.world = world;
        this.spawnX = spawnX;
        this.spawnY = spawnY;

        // initialize move speed
        this.movespeed = 1.5f;
        this.baseMovespeed = this.movespeed;

        setBounds(
            0 / testGame.PPM,
            0 / testGame.PPM,
            (playerTexture.getWidth() * scale)  / testGame.PPM,  // or maybe just getWidth() if you want pixel-size
            (playerTexture.getHeight() * scale) / testGame.PPM
        );

        this.world = world;
        setRegion(playerTexture);
        defineJosh();

        // Load and create animations
        loadStandAnimation();
        loadWalkAnimation();
        loadRunAnimation();
        loadHurtAnimation();
        loadDeathAnimation();

        // Default stance
        currentStance = Stance.STAND;
        stateTime = 0f;

        // Set initial sprite size to the first frame of your stand animation
        TextureRegion firstFrame = standAnimation.getKeyFrame(0);
        float width  = (firstFrame.getRegionWidth() * scale)  / testGame.PPM;
        float height = (firstFrame.getRegionHeight() * scale) / testGame.PPM;
        setBounds(0, 0, width, height);
        setRegion(firstFrame);
    }

    /**
     * Box2D definition for Josh
     */
    public void defineJosh() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(spawnX / testGame.PPM, spawnY / testGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius((float) 6 / testGame.PPM);

        // Add physical properties for better collision response
        fdef.shape = shape;
        fdef.density = 1.0f;
        fdef.restitution = 0.1f;  // Low bounce
        fdef.friction = 0.4f;     // Some friction for better control

        fdef.shape = shape;
        assert b2body != null;
        b2body.createFixture(fdef);

        shape.dispose();
    }

    // Load standing animation
    private void loadStandAnimation() {
        // Example: stand stance has 3 frames
        standTextures = new Texture[3];
        TextureRegion[] frames = new TextureRegion[3];

        for (int i = 0; i < 3; i++) {
            String fileName = parent_path + "idle" + "/" + "idle_knight_" + (i + 1) + ".png";
            standTextures[i] = new Texture(Gdx.files.internal(fileName));
            frames[i] = new TextureRegion(standTextures[i]);
        }

        standAnimation = new Animation<>(0.15f, frames);
        standAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    /**
     * Load the frames for the WALK stance and build the Animation.
     */
    private void loadWalkAnimation() {
        // Example: walk stance has 6 frames
        walkTextures = new Texture[8];
        TextureRegion[] frames = new TextureRegion[8];

        for (int i = 0; i < 8; i++) {
            String fileName = parent_path + "walk" + "/" + "walk_" + (i + 1) + ".png";
            walkTextures[i] = new Texture(Gdx.files.internal(fileName));
            frames[i] = new TextureRegion(walkTextures[i]);
        }

        walkAnimation = new Animation<>(0.1f, frames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    /**
     * Load the frames for the RUN stance and build the Animation.
     */
    private void loadRunAnimation() {
        // Example: run stance has 6 frames
        runTextures = new Texture[8];
        TextureRegion[] frames = new TextureRegion[8];

        for (int i = 0; i < 8; i++) {
            String fileName = parent_path + "run"+ "/" + "run_" + (i + 1) + ".png";
            runTextures[i] = new Texture(Gdx.files.internal(fileName));
            frames[i] = new TextureRegion(runTextures[i]);
        }

        runAnimation = new Animation<>(0.07f, frames);
        runAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    private void loadHurtAnimation() {
        hurtTextures = new Texture[8];  // Adjust number based on your frames
        TextureRegion[] frames = new TextureRegion[8];

        for (int i = 0; i < 8; i++) {
            String fileName = parent_path + "hurt/hurt_" + (i + 1) + ".png";
            hurtTextures[i] = new Texture(Gdx.files.internal(fileName));
            frames[i] = new TextureRegion(hurtTextures[i]);
        }

        float frameTime = hurtDuration / 12f;
        hurtAnimation = new Animation<>(frameTime, frames);
        hurtAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    private void loadDeathAnimation() {
        deathTextures = new Texture[12];  // Adjust number based on your frames
        TextureRegion[] frames = new TextureRegion[12];

        for (int i = 0; i < 12; i++) {
            String fileName = parent_path + "death/death_" + (i + 1) + ".png";
            deathTextures[i] = new Texture(Gdx.files.internal(fileName));
            frames[i] = new TextureRegion(deathTextures[i]);
        }

        deathAnimation = new Animation<>(deathDuration / 12f, frames);
        deathAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    /**
     * Called every frame by your game loop.
     */
    public void update(float dt) {
        stateTime += dt;

        // Pick the right Animation based on the current stance
        Animation<TextureRegion> currentAnimation = null;

        if (isHurt) {
            currentAnimation = hurtAnimation;
        } else {
            switch(currentStance) {
                case WALK:
                    currentAnimation = walkAnimation;
                    break;
                case RUN:
                    currentAnimation = runAnimation;
                    break;
                default:
                    currentAnimation = standAnimation;
                    break;
            }
        }

        if (currentAnimation != null) {
            // Get the current frame
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
            // Flip if needed
            if (!facingLeft && !currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            } else if (facingLeft && currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            }

            setRegion(currentFrame);
        }

        // Handle speed boost timer
        if (speedBoostActive) {
            speedBoostTimer += dt;
            if (speedBoostTimer >= speedBoostDuration) {
                speedBoostActive = false;
                setMovespeed(baseMovespeed);
            }
        }

        // Update sprite position to match Box2D body
        setPosition(
            b2body.getPosition().x - getWidth() / 2,
            b2body.getPosition().y - getHeight() / 2 + 3 / testGame.PPM
        );

        // When Josh is hurt
        if (isHurt) {
            hurtTimer += dt;
            TextureRegion hurtFrame = hurtAnimation.getKeyFrame(hurtTimer, false);
            if (!facingLeft && !hurtFrame.isFlipX()) {
                hurtFrame.flip(true, false);
            } else if (facingLeft && hurtFrame.isFlipX()) {
                hurtFrame.flip(true, false);
            }
            setRegion(hurtFrame);

            if (hurtTimer >= hurtDuration) {
                isHurt = false;
                stateTime = 0;
            }
            return;  // Skip normal animation updates while hurt
        }

        // When Josh is dead
        if (isDead) {
            deathTimer += dt;
            TextureRegion deathFrame = deathAnimation.getKeyFrame(deathTimer, false);
            if (!facingLeft && !deathFrame.isFlipX()) {
                deathFrame.flip(true, false);
            }
            setRegion(deathFrame);
            return;
        }

        // Update sprite
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        if (!facingLeft && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (facingLeft && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }
        setRegion(currentFrame);

    }

    /**
     * Change stance at runtime (e.g. from STAND to WALK).
     */
    public void setStance(Stance stance) {
        // If stance changes, we might reset the stateTime if you want the animation to restart
        // or keep the old stateTime if you prefer a seamless transition
        if (this.currentStance != stance) {
            this.currentStance = stance;
            this.stateTime = 0f;  // optional
        }
    }

    public void moveLeft() {
        facingLeft = true;
        // set the velocity or impulse if you want
        // b2body.setLinearVelocity(-someSpeed, b2body.getLinearVelocity().y);
    }

    public void moveRight() {
        facingLeft = false;
        // b2body.setLinearVelocity(someSpeed, b2body.getLinearVelocity().y);
    }

    public void damage() {
        if (!isHurt && !isDead) {
            health = Math.max(0, health - 1);
            if (health <= 0) {
                isDead = true;
                deathTimer = 0;
                b2body.setLinearVelocity(0, 0);  // Stop movement immediately
            } else {
                isHurt = true;
                hurtTimer = 0;
            }
        }
    }

    public boolean isDead() {
        return isDead;
    }

    /**
     * Helper and Disposes.
     */
    public void dispose() {
        // Dispose each array of textures
        for (Texture t : standTextures) { t.dispose(); }
        for (Texture t : walkTextures)  { t.dispose(); }
        for (Texture t : runTextures)   { t.dispose(); }
        for (Texture t : hurtTextures) { t.dispose(); }
    }

    public Vector2 getPosition() {
        return b2body.getPosition();
    }

    public Rectangle getBounds() {
        return new Rectangle(
            getX(),       // Sprite X position
            getY(),       // Sprite Y position
            getWidth(),   // Sprite width
            getHeight()   // Sprite height
        );
    }

    public void activateSpeedBoost() {
        if (!speedBoostActive) {
            speedBoostActive = true;
            speedBoostTimer = 0;
            movespeed = baseMovespeed * 1.5f; // Directly modify movespeed
        }
    }


    public void increaseHealth() {
        health++;
    }

    public void increaseSpeed(){
        setBasemovespeed(baseMovespeed * 1.5f);
    }

    public int getHealth() {
        return health;
    }

    public float getBasemovespeed() {
        return baseMovespeed;
    }

    public void setBasemovespeed(float basemovespeed) {
        this.baseMovespeed = basemovespeed;
    }

    public float getMovespeed() {
        return movespeed;
    }

    public void setMovespeed(float movespeed) {
        this.movespeed = movespeed;
    }

    public boolean hasKey() {
        return hasKey;
    }

    public void collectKey() {
        hasKey = true;
    }
}



//    public void defineJosh(){
//        BodyDef bdef = new BodyDef();
//        bdef.position.set((float) 32 / testGame.PPM, (float) 32 / testGame.PPM);
//        bdef.type = BodyDef.BodyType.DynamicBody;
//        b2body = world.createBody(bdef);
//
//        FixtureDef fdef = new FixtureDef();
//        CircleShape shape = new CircleShape();
//        shape.setRadius((float) 16 / testGame.PPM);
//
//        fdef.shape = shape;
//        assert b2body != null;
//        b2body.createFixture(fdef);
//    }

