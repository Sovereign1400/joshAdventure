package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.testGame;

public class DirectionalArrow extends Sprite {
    private Texture arrowTexture;
    private Josh player;
    private Door targetDoor;
    private static final float ARROW_OFFSET_Y = 16f / testGame.PPM;
    private static final float ARROW_SCALE = 0.5f;
    private static final float INITIAL_ARROW_ANGLE = 45f; // Since arrow points 45 degrees to top-left

    public DirectionalArrow(Josh player) {
        this.player = player;
        arrowTexture = new Texture("arrow/arrow.png");
        setRegion(arrowTexture);
        setSize(arrowTexture.getWidth() * ARROW_SCALE / testGame.PPM,
            arrowTexture.getHeight() * ARROW_SCALE / testGame.PPM);
        setOriginCenter();
    }

    public void update(Door nearestDoor) {
        this.targetDoor = nearestDoor;
        if (player != null && targetDoor != null) {
            // Position the arrow above the player
            setPosition(
                player.getPosition().x - getWidth() / 2,
                player.getPosition().y + ARROW_OFFSET_Y
            );

            // Get door center position
            float doorX = (targetDoor.getBoundsX() + targetDoor.getBoundsWidth() / 2) / testGame.PPM;
            float doorY = (targetDoor.getBoundsY() + targetDoor.getBoundsHeight() / 2) / testGame.PPM;

            // Get player position
            float playerX = player.getPosition().x;
            float playerY = player.getPosition().y;

            // Calculate direction vector
            float dx = doorX - playerX;
            float dy = doorY - playerY;

            // Calculate angle in radians, then convert to degrees
            float angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

            // Adjust angle to account for the initial 45-degree orientation of the arrow texture
            setRotation(angle - INITIAL_ARROW_ANGLE);
        }
    }

    public void draw(SpriteBatch batch) {
        if (player != null && targetDoor != null && !player.isDead()) {
            super.draw(batch);
        }
    }

    public void dispose() {
        if (arrowTexture != null) {
            arrowTexture.dispose();
        }
    }
}
