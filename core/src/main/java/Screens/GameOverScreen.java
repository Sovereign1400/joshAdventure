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


public class GameOverScreen implements Screen {

    private final testGame game;
    private Stage stage;
    private Skin skin;
    private com.badlogic.gdx.audio.Sound buttonSound;



    /**
     * You can pass data about the level or cause of game over if needed.
     */
    public GameOverScreen(testGame game) {
        this.game = game;
        buttonSound = Gdx.audio.newSound(Gdx.files.internal("audio/buttonSFX.mp3"));
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = game.getSkin();

        // Accept input on this stage
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.setBackground(skin.newDrawable("white", Color.FIREBRICK));

        game.playMusic.stop();
        game.pauseMusic.stop();

        game.gameOverMusic.setLooping(true);
        game.gameOverMusic.play();

        Label gameOverLabel = new Label("GAME OVER!", skin, "title");
        gameOverLabel.setColor(Color.RED);
        table.add(gameOverLabel).padBottom(30);
        table.row();

        // "Try Again?" button
        TextButton tryAgainButton = new TextButton("Try Again?", skin);
        tryAgainButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // For example, reload the same map or reset the level
                // If your last map was e.g. "customMap_2.tmx", do:
                game.setScreen(new PlayScreen(game, "tileset/customMap_2.tmx"));
            }
        });
        table.add(tryAgainButton).width(200).padBottom(10);
        table.row();

        // Main Menu
        TextButton mainMenuButton = new TextButton("Main Menu", skin);
        mainMenuButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        table.add(mainMenuButton).width(200).padBottom(10);
        table.row();

        // Exit
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
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
