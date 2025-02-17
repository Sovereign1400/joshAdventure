package Scenes;

import Sprites.Josh;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.testGame;
import com.badlogic.gdx.graphics.Color;

import static Screens.PlayScreen.SCORE_PER_SECOND;

/**
 * the HUD class is made to display some information on-screen that are relevant to the gameplay
 * hearts (health), time elapsed, score, and the collected key
 * it also keeps track of which world/level is currently active and shows current score
 */

public class HUD implements Disposable {
    public Stage stage;
    private Viewport viewport;
    private Integer elapsedTime;
    private float timeCount;
    private Integer score;


    // This set for updating world level in real time
    private int currentWorld = 1;
    private int currentLevel = 1;

    Label stopwatchLabel;
    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label mainCharLabel;
    // Label keyLabel;

    // These account for heart in HUD
    private Josh player;
    private Image[] heartImages;
    private Image keyImage;

    /**
     * Builds a new HUD that tracks time, score, and the player's health/key status.
     *
     * @param imgbatch used to draw UI, unused
     * @param player   The main player character so we can display health and key info regarding them
     */
    public HUD(SpriteBatch imgbatch, Josh player){
        this.player = player;
        elapsedTime = 0;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(testGame.win_width, testGame.win_height, new OrthographicCamera());
        stage = new Stage(viewport);

        Table table = new Table();
        table.top(); // Align to top
        table.setFillParent(true);

        // Initialize hearts
        heartImages = new Image[3];
        Texture heartTexture = new Texture("pickups/heart.png");

        Table heartTable = new Table();
        for(int i = 0; i < 3; i++) {
            heartImages[i] = new Image(heartTexture);
            heartTable.add(heartImages[i]).size(50, 50).padRight(30);
        }


        scoreLabel = new Label("SCORE: 000000", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        mainCharLabel = new Label("mainChar", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        // keyLabel = new Label("Key: Not Collected", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        stopwatchLabel = new Label("00:00:00", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // Score label
        scoreLabel = new Label("SCORE: 000000", new Label.LabelStyle(new BitmapFont(), Color.WHITE));


        Texture keyTexture = new Texture("pickups/key.png");
        keyImage = new Image(keyTexture);
        // Start darkened to indicate "not collected"
        keyImage.setColor(1, 1, 1, 0.2f);


        // Top row with original spacing
        table.add(heartTable).expandX().padTop(20);
        table.add(mainCharLabel).expandX().padTop(5);
        table.add(worldLabel).expandX().padTop(5);
        table.add(timeLabel).expandX().padTop(5);
        table.row();

        // Second row with original spacing
        table.add().expandX(); // Empty cell under heart table
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(stopwatchLabel).expandX();
        table.add().expandX(); // Empty cell for alignment
        table.row();

        // Key label in new row, aligned left
        // table.add(keyLabel).left().padLeft(10).padTop(5).colspan(4);

        /*table.add(heartTable).expandX().padTop(5);
        table.add(mainCharLabel).expandX().padTop(5);
        table.add(worldLabel).expandX().padTop(5);
        table.add(timeLabel).expandX().padTop(5);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();
        table.add(keyLabel).padTop(5).padLeft(10).row();*/

        table.add(keyImage).size(68, 90).left().padLeft(60).padTop(10).colspan(4);

        stage.addActor(table);
    }

    /**
     * the total elapsed time in seconds
     */
    public Integer getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Allows for manual time set up
     * the total elapsed time in seconds
     */
    public void setElapsedTime(Integer elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    /**
     * Increases the score by some amount
     * @param amount is the number of points that we add
     */
    public void addScore(int amount) {
        score += amount;
        scoreLabel.setText("SCORE: " + String.format("%06d", score));
    }

    /**
     * Updates the world level on the HUD, depends on the level loaded
     * @param mapPath A filename that we use to deduce the level
     */
    public void updateWorldLevel(String mapPath) {
        if (mapPath.contains("customMap_2")) {
            currentWorld = 1;
            currentLevel = 1;
        } else if (mapPath.contains("customMap_3")) {
            currentWorld = 1;
            currentLevel = 2;
        } else if (mapPath.contains("customMap_4")) {
            currentWorld = 1;
            currentLevel = 3;
        }
        levelLabel.setText(currentWorld + "-" + currentLevel);
    }

    /**
     * Our update method that is called each frame using dt. keeps track of hearts and key visibility
     * increases time if the game isn't paused
     *
     * @param dt     the time in seconds since last frame
     * @param paused true if the game is paused
     */
    public void update(float dt, boolean paused) {
        int health = player.getHealth();
        for (int i = 0; i < heartImages.length; i++) {
            heartImages[i].setVisible(i < health);
        }

        if (player.hasKey()) {
            keyImage.setColor(1, 1, 1, 1);
        } else {
            keyImage.setColor(1, 1, 1, 0.2f);
        }
        // check whether key is collected or not, also display it
        // keyLabel.setText(player.hasKey() ? "Key: Collected" : "Key: Not Collected");

        scoreLabel.setText("SCORE: " + String.format("%06d", score));

        // Update time only if not paused
        if (!paused) {
            timeCount += dt;
            if (timeCount >= 1) {
                elapsedTime++;
                timeCount = 0;

                // Convert to HH:MM:SS
                addScore(SCORE_PER_SECOND);
                int hours = elapsedTime / 3600;
                int remainder = elapsedTime % 3600;
                int minutes = remainder / 60;
                int seconds = remainder % 60;

                // Update the stopwatch label.
                stopwatchLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            }
        }
    }

    /**
     * @return the current total score
     */
    public int getScore() {
        return score;
    }

    /**
     * handles viewport resizing so the HUD scales with the screen
     *
     * @param width  new window width
     * @param height new window height
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * disposes of the screen when we're done so that resources are freed up
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
