package io.github.some_example_name;

import Screens.MainMenuScreen;
import Screens.PlayScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class testGame extends Game {
    public static final int win_width = 800;
    public static final int win_height = 800;
    public SpriteBatch batch;
    public static final float PPM = 32f;

//    GameMap gameMap;
    @Override
    public void create() {
        batch = new SpriteBatch();
        Stage stage = new Stage(new ScreenViewport());
        setScreen(new MainMenuScreen(this));

//        gameMap = new TiledGameMap();
    }

    @Override
//    public void render() {
//        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
//        batch.begin();
//        batch.draw(image, 140, 210);
//        batch.end();
//    }
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public Skin getSkin() {

        return new Skin(Gdx.files.internal("Skin123/pixthulhu-ui.json"));
    }
}
