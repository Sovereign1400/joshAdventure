package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.testGame;

/**
 * VictoryScreen displays a congratulatory message, plus buttons to Continue, go to Main Menu,
 * or Exit the application. Use this screen when the player successfully escapes the maze.
 */
public class VictoryScreen implements Screen {

    private final testGame game;
    private Stage stage;
    private Skin skin;
    private int finalScore;
    private com.badlogic.gdx.audio.Sound buttonSound;
    private Viewport viewport;

    /**
     * If you want to know from which level the user won, you could pass in more data here.
     */
    public VictoryScreen(testGame game, int finalScore) {
        this.game = game;
        viewport = new FitViewport(800, 480);
        stage = new Stage(viewport);
        // Calculate final score
        this.finalScore = finalScore;
        buttonSound = Gdx.audio.newSound(Gdx.files.internal("audio/buttonSFX.mp3"));
    }

    @Override
    public void show() {
        // Create the stage
        stage = new Stage(viewport);
        // Load skin from the game or from your assets directly
        skin = game.getSkin();

        // Make sure stage gets input
        Gdx.input.setInputProcessor(stage);

        // Build UI
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Background color (placeholder)
        table.setBackground(skin.newDrawable("white", Color.FOREST));

        //Music
        game.playMusic.stop();
        game.pauseMusic.stop();

        game.victoryMusic.setLooping(true);
        game.victoryMusic.play();
        game.playMusic.setVolume(0.2f);

        // Title
        Label winLabel = new Label("YOU WIN!", skin, "title");
        winLabel.setColor(Color.GOLD);
        table.add(winLabel).padBottom(30);
        table.row();

        // Show final score
        Label scoreLabel = new Label("FINAL SCORE: " + finalScore, skin);
        table.add(scoreLabel).padBottom(30);
        table.row();

        // "Continue?" button
        // This might load the next map or next stage if you have multiple levels
        TextButton continueButton = new TextButton("Continue?", skin);
        continueButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Example: load another map
                // (Or if you have a "next stage" logic, call that)
                game.setScreen(new PlayScreen(game, "tileset/customMap_3.tmx"));
            }
        });
        table.add(continueButton).width(200).padBottom(10);
        table.row();

        // Main Menu button
        TextButton mainMenuButton = new TextButton("Main Menu", skin);
        mainMenuButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        table.add(mainMenuButton).width(200).padBottom(10);
        table.row();

        // Exit button
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(exitButton).width(200);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update + draw stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        game.victoryMusic.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
