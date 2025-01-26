package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.testGame;
import Scenes.HUD;
import Sprites.*;
import Tools.B2WorldCreator;
import Tools.WorldContactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.GL20;


public class GameOverScreen implements Screen {
    private testGame game;
    private Stage stage;
    private Viewport viewport;
    private Table table;
    private Label titleLabel;
    private TextButton restartButton, exitButton;
    private Skin skin;

    public GameOverScreen(testGame game) {
        this.game = game;
        viewport = new FitViewport(testGame.win_width, testGame.win_height, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        // Create skin for UI elements
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        createMenu();
        Gdx.input.setInputProcessor(stage);
    }

    private void createMenu() {
        table = new Table();
        table.setFillParent(true);

        // Create title
        Label.LabelStyle titleStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        titleStyle.font.getData().setScale(2);
        titleLabel = new Label("Game Over!", titleStyle);

        // Create buttons
        restartButton = new TextButton("Restart", skin);
        exitButton = new TextButton("Exit", skin);

        // Add listeners
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Add to table with spacing
        table.add(titleLabel).padBottom(50).row();
        table.add(restartButton).width(200).height(50).padBottom(20).row();
        table.add(exitButton).width(200).height(50);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
