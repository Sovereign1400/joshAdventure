package io.github.some_example_name;

import Screens.PlayScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class testGame extends Game {
    public static final int win_width = 800;
    public static final int win_height = 800;
    public SpriteBatch batch;
    public static final float PPM = 32;

//    GameMap gameMap;
    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new PlayScreen(this));

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
}
