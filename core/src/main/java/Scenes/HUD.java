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

public class HUD implements Disposable {
    public Stage stage;
    private Viewport viewport;
    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    Label countdownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label mainCharLabel;

    // These account for heart in HUD
    private Josh player;
    private Image[] heartImages;


    public HUD(SpriteBatch imgbatch, Josh player){
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.player = player;
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(testGame.win_width, testGame.win_height, new OrthographicCamera());
        stage = new Stage(viewport, imgbatch);

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);

        // Initialize hearts
        heartImages = new Image[3];
        Texture heartTexture = new Texture("pickups/heart.png");

        Table heartTable = new Table();
        for(int i = 0; i < 3; i++) {
            heartImages[i] = new Image(heartTexture);
            heartTable.add(heartImages[i]).size(30, 50).padRight(10);
        }

        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        mainCharLabel = new Label("mainChar", new Label.LabelStyle(new BitmapFont(), Color.WHITE));



        table.add(heartTable).expandX().padTop(5);
        table.add(mainCharLabel).expandX().padTop(5);
        table.add(worldLabel).expandX().padTop(5);
        table.add(timeLabel).expandX().padTop(5);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(table);
    }

    public void update() {
        int health = player.getHealth();
        for (int i = 0; i < heartImages.length; i++) {
            heartImages[i].setVisible(i < health);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
