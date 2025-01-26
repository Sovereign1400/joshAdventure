package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

    public Monster(World world, float x, float y, Josh player) {
        this.world = world;
        this.player = player;
        this.initialX = x / testGame.PPM;

        monsterTexture = new Texture("monster.png");
        setBounds(0, 0, 16 / testGame.PPM, 16 / testGame.PPM);
        setRegion(monsterTexture);

        defineMonster();
        b2body.setTransform(x / testGame.PPM, y / testGame.PPM, 0);
    }

    private void defineMonster() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.fixedRotation = true;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / testGame.PPM);

        fdef.shape = shape;
//        fdef.density = 0.1f;
//        b2body.createFixture(fdef);
//        fdef.isSensor = true;  // Makes it trigger collision without physical response
        b2body.createFixture(fdef).setUserData(this);  // Set user data for collision detection
        shape.dispose();
    }

    public void update(float dt) {
        Vector2 position = b2body.getPosition();
        Vector2 playerPosition = player.b2body.getPosition();  // Need to pass player reference

        // Calculate direction to player
        Vector2 direction = new Vector2(
            playerPosition.x - position.x,
            playerPosition.y - position.y
        );
        direction.nor();  // Normalize the direction vector

        // Set monster velocity towards player
        float targetVelocity = 0.2f;  // Adjust speed as needed
        b2body.setLinearVelocity(
            direction.x * targetVelocity,
            direction.y * targetVelocity
        );

        setPosition(position.x - getWidth() / 2, position.y - getHeight() / 2);
        setFlip(direction.x < 0, false);  // Face movement direction
    }

    public void dispose() {
        monsterTexture.dispose();
    }
}
