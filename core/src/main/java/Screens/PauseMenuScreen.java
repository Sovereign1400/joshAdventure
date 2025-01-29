package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.testGame;

/**
 * The PauseMenuScreen class appears when the player presses Esc during gameplay.
 * It pauses the game, stops or switches background music if desired, and offers:
 *  - CONTINUE (resume the game)
 *  - LOAD NEW MAP (start a new game)
 *  - STAGE SELECT (sub-menu for stage1,2,3)
 *  - EXIT (quit the application)
 */
public class PauseMenuScreen implements Screen {

    private final testGame game;         // Reference to main game
    private final PlayScreen gameScreen; // The screen we paused, so we can resume
    private Stage pauseStage;            // The main pause menu
    private Stage stageSelectLevel;      // Sub-stage for Stage 1/2/3
    private Stage activeStage;           // Whichever is currently displayed
    private com.badlogic.gdx.audio.Sound buttonSound;
    private Skin skin;

    /**
     * @param game       The core game instance
     * @param gameScreen The PlayScreen that was paused
     */
    public PauseMenuScreen(testGame game, PlayScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        buttonSound = Gdx.audio.newSound(Gdx.files.internal("audio/buttonSFX.mp3"));
    }

    @Override
    public void show() {
        // Mark the game screen as paused
        if (gameScreen != null) {
            gameScreen.setPaused(true);
            // If you have music, you could do: game.pauseMusic();
        }

        skin = game.getSkin();

        // Create the 2 stages
        pauseStage = new Stage(new ScreenViewport());
        stageSelectLevel = new Stage(new ScreenViewport());

        createPauseStage();
        createStageSelectLevel();

        activeStage = pauseStage;
        Gdx.input.setInputProcessor(activeStage);
    }

    /**
     * Creates the pause menu with 4 buttons:
     *  - CONTINUE, LOAD NEW MAP, STAGE SELECT, EXIT
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
                buttonSound.play();
                // Unpause and go back
                if (gameScreen != null) {
                    gameScreen.setPaused(false);
                    // If you paused music, resume here: game.playMusic();
                    game.setScreen(gameScreen);
                }
            }
        });
        table.add(continueButton).row();

        // Load New Map
        TextButton loadMapButton = new TextButton("Load New Map", skin);
        loadMapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Replace the game screen with a fresh one
                buttonSound.play();
                game.setScreen(new PlayScreen(game, "anotherMap.tmx"));
            }
        });
        table.add(loadMapButton).row();

        // Stage Select
        TextButton stageSelectButton = new TextButton("Stage Select", skin);
        stageSelectButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Switch to the stage select sub-stage
                buttonSound.play();
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
                buttonSound.play();
                Gdx.app.exit();
            }
        });
        table.add(exitButton).row();
    }

    /**
     * Creates a sub-stage for selecting Stage 1/2/3, plus a Back button to return.
     */
    private void createStageSelectLevel() {
        Table table = new Table();
        table.setFillParent(true);
        stageSelectLevel.addActor(table);

        table.setBackground(skin.newDrawable("white", Color.FOREST));
        table.defaults().pad(15);

        Label title = new Label("Choose a Stage", skin, "title");
        table.add(title).padBottom(30).row();

        // Stage 1
        TextButton stage1Button = new TextButton("Stage 1", skin);
        stage1Button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Load stage1
                game.setScreen(new PlayScreen(game, "stage1.tmx"));
                buttonSound.play();
            }
        });
        table.add(stage1Button).width(200).row();

        // Stage 2
        TextButton stage2Button = new TextButton("Stage 2", skin);
        stage2Button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Load stage2
                game.setScreen(new PlayScreen(game, "stage2.tmx"));
                buttonSound.play();
            }
        });
        table.add(stage2Button).width(200).row();

        // Stage 3
        TextButton stage3Button = new TextButton("Stage 3", skin);
        stage3Button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Load stage3
                game.setScreen(new PlayScreen(game, "stage3.tmx"));
                buttonSound.play();
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
                buttonSound.play();
            }
        });
        table.add(backButton).width(200).padTop(20);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        activeStage.act(delta);
        activeStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        pauseStage.getViewport().update(width, height, true);
        stageSelectLevel.getViewport().update(width, height, true);

    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        pauseStage.dispose();
        stageSelectLevel.dispose();
    }
}
