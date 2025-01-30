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
 * This class is for the pausescreen which appears when the player presses ESC during the gameplay
 * It pauses the game and the following buttons are offered:
 *  continue (resumes the game)
 *  level select (allows us to load one of the maps in the game and lets us start from whichever we choose)
 *  exit (closes the game)
 */
public class PauseMenuScreen implements Screen {

    private final testGame game;         // Reference to main game
    private final PlayScreen gameScreen; // The screen we paused, so we can resume
    private Stage pauseStage;            // The main pause menu
    private Stage stageSelectLevel;      // Sub-stage for Stage 1/2/3
    private Stage activeStage;           // Whichever is currently displayed
    private com.badlogic.gdx.audio.Sound buttonSound;
    private Skin skin;
    private Viewport viewport;
    private Stage stage;

    /**
     * builds the pause menu screen,
     * initializes everything, and sets up a viewport
     * in order to fit everything in any sized window
     *
     */
    public PauseMenuScreen(testGame game, PlayScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;

        // Create a FitViewport for all UI so it scales correctly on resize
        viewport = new FitViewport(1280, 720);

        // Create the two stages (pause menu & stage select) right here, so they're only created once
        pauseStage = new Stage(viewport);
        stageSelectLevel = new Stage(viewport);

        // The "stage" field is unused, but we'll keep it to avoid removing your comments
        stage = new Stage(viewport);

        buttonSound = Gdx.audio.newSound(Gdx.files.internal("audio/buttonSFX.mp3"));
    }

    /**
     * called just after the screen has been set to active
     * sets up the UI and music
     */
    @Override
    public void show() {
        // Mark the game screen as paused
        if (gameScreen != null) {
            gameScreen.setPaused(true);
            // If you have music, you could do: game.pauseMusic();
        }

        skin = game.getSkin();

        // Create the 2 stages
        // pauseStage = new Stage(viewport);       // <--- Commented out to avoid re-initializing
        // stageSelectLevel = new Stage(viewport); // <--- Same reason

        createPauseStage();
        createStageSelectLevel();

        activeStage = pauseStage;
        Gdx.input.setInputProcessor(activeStage);
        game.pauseMusic.setLooping(true);
        game.pauseMusic.play();
        game.pauseMusic.setVolume(0.3f);
    }

    /**
     * creates the pause menu with 4 buttons, each with different functions
     */
    private void createPauseStage() {
        Table table = new Table();
        table.setFillParent(true);
        pauseStage.addActor(table);

        // Semi-transparent background
        table.setBackground(skin.newDrawable("white", new Color(0,0,0,0.7f)));
        table.defaults().pad(10);

        Label pauseLabel = new Label("Game Paused", skin, "title");
        table.add(pauseLabel).padBottom(20).row();

        // Continue
        TextButton continueButton = new TextButton("Continue", skin);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonSound.play(0.15F);
                // Unpause and go back
                if (gameScreen != null) {
                    gameScreen.setPaused(false);
                    game.pauseMusic.stop();
                    game.playMusic.play();
                    game.setScreen(gameScreen);
                }
            }
        });
        table.add(continueButton).row();

        // Stage Select
        TextButton stageSelectButton = new TextButton("Level Select", skin);
        stageSelectButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonSound.play(0.15F);
                activeStage = stageSelectLevel;
                Gdx.input.setInputProcessor(stageSelectLevel);
            }
        });
        table.add(stageSelectButton).row();

        // Exit
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonSound.play(0.15F);
                Gdx.app.exit();
            }
        });
        table.add(exitButton).row();
    }

    /**
     * creates a separate substage and lets us select a level and load a map file
     * each button loads a new map in a fresh game
     * back button to return to pause menu
     */
    private void createStageSelectLevel() {
        Table table = new Table();
        table.setFillParent(true);
        stageSelectLevel.addActor(table);

        table.setBackground(skin.newDrawable("white", Color.FOREST));
        table.defaults().pad(15);

        Label title = new Label("Choose a Level", skin, "title");
        table.add(title).padBottom(30).row();

        // Stage 1
        TextButton stage1Button = new TextButton("Level 1", skin);
        stage1Button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Load stage1
                game.setScreen(new PlayScreen(game, "tileset/customMap_2.tmx"));
                buttonSound.play(0.15F);
                game.pauseMusic.stop();
                game.playMusic.play();
            }
        });
        table.add(stage1Button).width(200).row();

        // Stage 2
        TextButton stage2Button = new TextButton("Level 2", skin);
        stage2Button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Load stage2
                game.setScreen(new PlayScreen(game, "tileset/customMap_3.tmx"));
                buttonSound.play(0.15F);
                game.pauseMusic.stop();
                game.playMusic.play();
            }
        });
        table.add(stage2Button).width(200).row();

        // Stage 3
        TextButton stage3Button = new TextButton("Level 3", skin);
        stage3Button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Load stage3
                game.setScreen(new PlayScreen(game, "tileset/customMap_4.tmx"));
                buttonSound.play(0.15F);
                game.pauseMusic.stop();
                game.playMusic.play();
            }
        });
        table.add(stage3Button).width(200).row();

        // Back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activeStage = pauseStage;
                Gdx.input.setInputProcessor(pauseStage);
                buttonSound.play(0.15F);
            }
        });
        table.add(backButton).width(200).padTop(20);
    }

    /**
     * draws whichever stage is active (pause menu or level select).
     * delta time since last frame in seconds
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        activeStage.act(delta);
        activeStage.draw();
    }

    /**
     * ensures UI scales properly if the window size changes
     */
    @Override
    public void resize(int width, int height) {
        pauseStage.getViewport().update(width, height, true);
        stageSelectLevel.getViewport().update(width, height, true);
    }

    /**
     * Called when the game is paused
     */
    @Override
    public void pause() { }

    /**
     * Called when the game is resumed
     */
    @Override
    public void resume() { }

    /**
     * Called when this screen is hidden and stops input/music
     */
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        game.pauseMusic.stop();
    }

    /**
     * disposes of the screen when we're done so that resources are freed up
     */
    @Override
    public void dispose() {
        pauseStage.dispose();
        stageSelectLevel.dispose();
    }
}
