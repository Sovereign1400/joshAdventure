package io.github.some_example_name;

import Screens.MainMenuScreen;
import Screens.PlayScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class testGame extends Game {
    public static final int win_width = 1280;
    public static final int win_height = 720;
    public SpriteBatch batch;
    public static final float PPM = 32f;
    private Skin skin;
    public Music mainMenuMusic;
    public Music playMusic;
    public Music pauseMusic;
    public Music victoryMusic;
    public Music gameOverMusic;

//    GameMap gameMap;
    @Override
    public void create() {

        // Set window size
        Gdx.graphics.setWindowedMode(win_width, win_height);


        batch = new SpriteBatch();

//        gameMap = new TiledGameMap();
        mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/mainmenuBG.mp3"));
        playMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/gameplayBG.mp3"));
        pauseMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/pauseBG.mp3"));
        victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/victoryBG.mp3"));
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/gameoverBG.mp3"));

        skin = getSkin();
        setScreen(new MainMenuScreen(this));

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
        mainMenuMusic.dispose();
        playMusic.dispose();
        pauseMusic.dispose();
        victoryMusic.dispose();
        gameOverMusic.dispose();
        super.dispose();
    }

    public Skin getSkin() {
        return new Skin(Gdx.files.internal("Skin123/pixthulhu-ui.json"));
    }
}
