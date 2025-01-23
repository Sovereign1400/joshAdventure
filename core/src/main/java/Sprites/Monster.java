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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.some_example_name.testGame;

public class Monster extends Sprite {
    // The physical body in the Box2D world
    public Body b2body;
    private World world;

    // Visual elements
    private Texture monsterTexture;

    // Movement properties
    private float movementSpeed = 1f;
    private float movementDistance = 2f;
    private float initialX;
    private boolean movingRight = true;

    public Monster(World world, float x, float y) {
        this.world = world;
        this.initialX = x / testGame.PPM;

        // Set up the monster's visual appearance
        monsterTexture = new Texture("monster.png");
        setBounds(0, 0, 16 / testGame.PPM, 16 / testGame.PPM);
        setRegion(monsterTexture);

        // Create the physical body and set its initial position
        defineMonster();
        b2body.setTransform(x / testGame.PPM, y / testGame.PPM, 0);
    }

    private void defineMonster() {
        // Create the body definition
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.KinematicBody;
        b2body = world.createBody(bdef);

        // Set up collision shape
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / testGame.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    public void update(float dt) {
        // Calculate the monster's movement range
        Vector2 position = b2body.getPosition();
        float leftBound = initialX - movementDistance;
        float rightBound = initialX + movementDistance;

        // Change direction when reaching movement bounds
        if (position.x >= rightBound) {
            movingRight = false;
        } else if (position.x <= leftBound) {
            movingRight = true;
        }

        // Set the monster's movement velocity
        float velocityX = movingRight ? movementSpeed : -movementSpeed;
        b2body.setLinearVelocity(velocityX, 0);

        // Update the sprite's position to match the physical body
        setPosition(
            b2body.getPosition().x - getWidth() / 2,
            b2body.getPosition().y - getHeight() / 2
        );

        // Flip the sprite based on movement direction
        setFlip(!movingRight, false);
    }

    public void dispose() {
        monsterTexture.dispose();
    }
}
