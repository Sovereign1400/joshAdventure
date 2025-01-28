package Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;

public class Monster extends Sprite {
    public Body b2body;
    private World world;
    private Texture monsterTexture;
    private float initialX;
    private float moveDistance = 2f;
    private float moveSpeed = 2f;
    private boolean movingRight = true;
    private Josh player;
    private String parent_path = "samurai/";  // Path to monster animations
    private float stateTime;
    private boolean facingLeft = true;

    // Different animations for monster states
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> idleAnimation;

    // Store textures for disposal
    private Texture[] walkTextures;
    private Texture[] attackTextures;
    private Texture[] idleTextures;

    // Monster states
    public enum MonsterState {
        IDLE,
        WALK,
        ATTACK
    }
    private MonsterState currentState;
    private MonsterState previousState;

    // Constructor
    public Monster(World world, float x, float y, Josh player) {
        this.world = world;
        this.player = player;
        this.initialX = x / testGame.PPM;

//        monsterTexture = new Texture("monster.png");
//        setBounds(0, 0, 16 / testGame.PPM, 16 / testGame.PPM);
//        setRegion(monsterTexture);
//
//        defineMonster();
//        b2body.setTransform(x / testGame.PPM, y / testGame.PPM, 0);
        this.world = world;
        this.player = player;
        stateTime = 0;
        currentState = MonsterState.IDLE;
        previousState = MonsterState.IDLE;

        // Load all animations
        loadIdleAnimation();
        loadWalkAnimation();
        loadAttackAnimation();

        // Set initial frame
        TextureRegion firstFrame = idleAnimation.getKeyFrame(0);
        float scale = 0.5f;  // Adjust scale as needed
        setBounds(0, 0,
            (firstFrame.getRegionWidth() * scale) / testGame.PPM,
            (firstFrame.getRegionHeight() * scale) / testGame.PPM);
        setRegion(firstFrame);

        defineMonster(x, y);
    }

    private void defineMonster(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x / testGame.PPM, y / testGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / testGame.PPM);

        fdef.shape = shape;
        fdef.density = 1.0f;
        fdef.restitution = 0.1f;
        fdef.friction = 0.4f;

        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    public void update(float dt) {
        stateTime += dt;

        Vector2 position = b2body.getPosition();
        Vector2 playerPosition = player.b2body.getPosition();

        // Calculate direction to player
        Vector2 direction = new Vector2(
            playerPosition.x - position.x,
            playerPosition.y - position.y
        );
        float distanceToPlayer = direction.len();
        direction.nor();  // Normalize the direction vector

        // Update monster state based on distance to player
        if (distanceToPlayer < 1f) {
            currentState = MonsterState.ATTACK;
        } else if (distanceToPlayer < 5f) {
            currentState = MonsterState.WALK;
        } else {
            currentState = MonsterState.IDLE;
        }

        // Get current animation frame based on state
        Animation<TextureRegion> currentAnimation = null;
        switch(currentState) {
            case WALK:
                currentAnimation = walkAnimation;
                // Keep your existing movement code
                float targetVelocity = 0.2f;
                b2body.setLinearVelocity(
                    direction.x * targetVelocity,
                    direction.y * targetVelocity
                );
                break;
            case ATTACK:
                currentAnimation = attackAnimation;
                // Could add attack logic here
                break;
            case IDLE:
            default:
                currentAnimation = idleAnimation;
                b2body.setLinearVelocity(0, 0);
                break;
        }

        // Update sprite position
        setPosition(position.x - getWidth() / 2, position.y - getHeight() / 2);

        // Update sprite animation frame
        if (currentAnimation != null) {
            TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
            // Handle sprite flipping based on movement direction
            if (direction.x < 0 && !frame.isFlipX()) {
                frame.flip(true, false);
            } else if (direction.x > 0 && frame.isFlipX()) {
                frame.flip(true, false);
            }
            setRegion(frame);
        }
    }

    // Loading animations
    private void loadIdleAnimation() {
        // Example: idle has 8 frames
        idleTextures = new Texture[10];
        TextureRegion[] frames = new TextureRegion[10];

        for (int i = 0; i < 10; i++) {
            String fileName = parent_path + "idle/idle_" + (i + 1) + ".png";
            idleTextures[i] = new Texture(Gdx.files.internal(fileName));
            frames[i] = new TextureRegion(idleTextures[i]);
        }

        idleAnimation = new Animation<>(0.1f, frames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    private void loadWalkAnimation() {
        // Example: walk has 8 frames
        walkTextures = new Texture[9];
        TextureRegion[] frames = new TextureRegion[9];

        for (int i = 0; i < 9; i++) {
            String fileName = parent_path + "walk/sprite_" + (i + 1) + ".png";
            walkTextures[i] = new Texture(Gdx.files.internal(fileName));
            frames[i] = new TextureRegion(walkTextures[i]);
        }

        walkAnimation = new Animation<>(0.1f, frames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    private void loadAttackAnimation() {
        // Example: attack has 8 frames
        attackTextures = new Texture[7];
        TextureRegion[] frames = new TextureRegion[7];

        for (int i = 0; i < 7; i++) {
            String fileName = parent_path + "attack/attack_" + (i + 1) + ".png";
            attackTextures[i] = new Texture(Gdx.files.internal(fileName));
            frames[i] = new TextureRegion(attackTextures[i]);
        }

        attackAnimation = new Animation<>(0.1f, frames);
        attackAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void dispose() {
//        monsterTexture.dispose();
        // Dispose all animation textures
        if (walkTextures != null) {
            for (Texture texture : walkTextures) {
                if (texture != null) texture.dispose();
            }
        }
        if (attackTextures != null) {
            for (Texture texture : attackTextures) {
                if (texture != null) texture.dispose();
            }
        }
        if (idleTextures != null) {
            for (Texture texture : idleTextures) {
                if (texture != null) texture.dispose();
            }
        }
    }
}
